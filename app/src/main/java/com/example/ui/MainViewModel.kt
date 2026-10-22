package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiVoiceService
import com.example.audio.AudioEngine
import com.example.audio.DspAudioProcessor
import com.example.data.local.VocalForgeDatabase
import com.example.data.model.*
import com.example.data.voices.VoiceCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.ArrayDeque
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication<Application>().applicationContext
    val audioEngine = AudioEngine(context)
    private val geminiService = GeminiVoiceService()
    private val db = VocalForgeDatabase.getDatabase(context)

    val allProjects: StateFlow<List<AudioProject>> = db.projectDao().getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRules: StateFlow<List<PronunciationRule>> = db.pronunciationDao().getAllRules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clonedVoices: StateFlow<List<ClonedVoice>> = db.clonedVoiceDao().getAllClones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Navigation Workspace
    private val _currentWorkspace = MutableStateFlow(StudioWorkspace.TTS_STUDIO)
    val currentWorkspace = _currentWorkspace.asStateFlow()

    // Navigation back stack for Android back button
    private val workspaceBackStack = ArrayDeque<StudioWorkspace>()

    // Global Status / Snackbar Message
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage = _statusMessage.asStateFlow()

    // Generation Progress State (e.g. for 20,000–30,000 words large scripts)
    private val _isProcessingJob = MutableStateFlow(false)
    val isProcessingJob = _isProcessingJob.asStateFlow()

    private val _processingProgress = MutableStateFlow(0f)
    val processingProgress = _processingProgress.asStateFlow()

    private val _processingStatusText = MutableStateFlow("")
    val processingStatusText = _processingStatusText.asStateFlow()

    // Selected Voice for TTS & Audition
    private val _selectedVoice = MutableStateFlow(VoiceCatalog.allVoices.first())
    val selectedVoice = _selectedVoice.asStateFlow()

    // Active working audio file for Lab tools (Noise Removal, Enhancement, Editor, Changer)
    private val _activeAudioFile = MutableStateFlow<File?>(null)
    val activeAudioFile = _activeAudioFile.asStateFlow()

    private val _processedAudioFile = MutableStateFlow<File?>(null)
    val processedAudioFile = _processedAudioFile.asStateFlow()

    // Multi-character story lines
    private val _storyLines = MutableStateFlow<List<DialogueLine>>(emptyList())
    val storyLines = _storyLines.asStateFlow()

    // Story background ambience
    private val _selectedAmbience = MutableStateFlow<DspAudioProcessor.SfxType?>(null)
    val selectedAmbience = _selectedAmbience.asStateFlow()

    // Theme mode
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    init {
        workspaceBackStack.push(StudioWorkspace.TTS_STUDIO)
        loadSampleStory()
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun navigateTo(workspace: StudioWorkspace) {
        if (_currentWorkspace.value != workspace) {
            workspaceBackStack.push(_currentWorkspace.value)
            _currentWorkspace.value = workspace
        }
    }

    fun handleBackPress(): Boolean {
        if (workspaceBackStack.isNotEmpty()) {
            val previous = workspaceBackStack.pop()
            _currentWorkspace.value = previous
            return true
        }
        return false
    }

    fun selectVoice(voice: VoiceProfile) {
        _selectedVoice.value = voice
    }

    fun auditionVoice(voice: VoiceProfile) {
        viewModelScope.launch {
            val auditionText = "Hello! I am ${voice.name}, ready to bring your words to life in VocalForge Studio."
            val testFile = File(context.cacheDir, "audition_${voice.id}.wav")
            audioEngine.stopAudio()
            val success = audioEngine.synthesizeTextToFile(auditionText, voice, testFile)
            if (success && testFile.exists()) {
                audioEngine.playAudio(testFile.absolutePath)
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    // TTS STUDIO: Generate Voice
    fun generateTts(
        text: String,
        voice: VoiceProfile,
        pitchOffset: Float = 0f,
        speedOffset: Float = 0f,
        title: String = "Untitled Speech"
    ) {
        if (text.isBlank()) {
            _statusMessage.value = "Please enter some text to synthesize."
            return
        }

        viewModelScope.launch {
            _isProcessingJob.value = true
            _processingStatusText.value = "Applying pronunciation rules & synthesizing..."
            _processingProgress.value = 0.2f

            // Apply Pronunciation Dictionary replacements
            var preparedText = text
            val rules = allRules.value
            for (rule in rules) {
                if (rule.isEnabled) {
                    preparedText = if (rule.isCaseSensitive) {
                        preparedText.replace(rule.wordToReplace, rule.phoneticReplacement)
                    } else {
                        preparedText.replace(Regex(Regex.escape(rule.wordToReplace), RegexOption.IGNORE_CASE), rule.phoneticReplacement)
                    }
                }
            }

            val outputFile = File(context.filesDir, "tts_${System.currentTimeMillis()}.wav")

            // Check if long script (> 400 chars)
            val isLongScript = preparedText.length > 400
            val success = if (isLongScript) {
                audioEngine.processLargeScript(preparedText, voice, outputFile) { current, total, msg ->
                    _processingProgress.value = current.toFloat() / total.toFloat()
                    _processingStatusText.value = msg
                }
            } else {
                audioEngine.synthesizeTextToFile(preparedText, voice, outputFile, pitchOffset, speedOffset)
            }

            _isProcessingJob.value = false
            if (success && outputFile.exists()) {
                val words = preparedText.trim().split(Regex("\\s+")).size
                val wavData = DspAudioProcessor.readWavFile(outputFile)
                val durationMs = if (wavData != null && wavData.sampleRate > 0) {
                    (wavData.samples.size.toDouble() / wavData.sampleRate * 1000.0).toLong()
                } else 3000L

                val project = AudioProject(
                    title = title.ifBlank { "Speech (${voice.name})" },
                    scriptText = text,
                    voiceId = voice.id,
                    voiceName = voice.name,
                    audioFilePath = outputFile.absolutePath,
                    durationMs = durationMs,
                    wordCount = words,
                    workspaceType = "TTS",
                    fileSizeBytes = outputFile.length()
                )
                db.projectDao().insertProject(project)

                _activeAudioFile.value = outputFile
                _statusMessage.value = "Generation completed! Playing audio..."
                audioEngine.playAudio(outputFile.absolutePath)
            } else {
                _statusMessage.value = "Speech synthesis encountered an issue. Please verify input text."
            }
        }
    }

    // MULTI-CHARACTER STORY STUDIO
    private fun loadSampleStory() {
        val sample = """
            Narrator: The obsidian door opened with a slow, grinding whisper in the midnight fog.
            Elena (whispering): Did you hear that Marcus? We shouldn't be here.
            Marcus (calm): Stay close. The quantum relay is inside this chamber.
            AI Core: Warning. Biological intrusion detected on Sector Nine.
            Narrator (excited): Suddenly, crimson emergency runes ignited along the stone walls!
        """.trimIndent()
        parseStoryScript(sample)
    }

    fun parseStoryScript(scriptText: String) {
        viewModelScope.launch {
            _processingStatusText.value = "Analyzing dialogue characters & emotional tone..."
            _isProcessingJob.value = true
            val parsed = geminiService.parseMultiCharacterScript(scriptText)

            // Auto-assign distinct voices to characters
            val assignedVoices = mapOf(
                "narrator" to "doc_01",
                "elena" to "pod_02",
                "marcus" to "bass_01",
                "ai core" to "sci_01"
            )

            for (line in parsed) {
                val voiceKey = line.speaker.lowercase().trim()
                if (assignedVoices.containsKey(voiceKey)) {
                    line.voiceId = assignedVoices[voiceKey]!!
                }
            }

            _storyLines.value = parsed
            _isProcessingJob.value = false
            _statusMessage.value = "Identified ${parsed.size} dialogue turns across speakers."
        }
    }

    fun updateDialogueVoice(lineId: String, voiceId: String) {
        _storyLines.value = _storyLines.value.map {
            if (it.id == lineId) it.copy(voiceId = voiceId) else it
        }
    }

    fun updateDialogueEmotion(lineId: String, emotion: String) {
        _storyLines.value = _storyLines.value.map {
            if (it.id == lineId) it.copy(emotion = emotion) else it
        }
    }

    fun setAmbience(type: DspAudioProcessor.SfxType?) {
        _selectedAmbience.value = type
    }

    fun synthesizeSingleStoryLine(line: DialogueLine) {
        viewModelScope.launch {
            val voice = VoiceCatalog.getVoiceById(line.voiceId)
            val lineFile = File(context.cacheDir, "story_line_${line.id}.wav")
            val textWithEmotion = if (line.emotion != "Neutral") "[${line.emotion.lowercase()}] ${line.text}" else line.text
            val success = audioEngine.synthesizeTextToFile(textWithEmotion, voice, lineFile)
            if (success && lineFile.exists()) {
                line.audioPath = lineFile.absolutePath
                _statusMessage.value = "Auditioning line for ${line.speaker}..."
                audioEngine.playAudio(lineFile.absolutePath)
            }
        }
    }

    fun assembleCompleteStory(title: String = "Multi-Character Production") {
        val lines = _storyLines.value
        if (lines.isEmpty()) return

        viewModelScope.launch {
            _isProcessingJob.value = true
            val tempDir = File(context.cacheDir, "story_assembly_${System.currentTimeMillis()}").apply { mkdirs() }
            val renderedParts = mutableListOf<File>()

            for ((i, line) in lines.withIndex()) {
                _processingProgress.value = (i.toFloat() / lines.size) * 0.8f
                _processingStatusText.value = "Voicing ${line.speaker} (${i + 1}/${lines.size})..."

                val voice = VoiceCatalog.getVoiceById(line.voiceId)
                val lineFile = File(tempDir, "line_$i.wav")
                val textWithEmotion = if (line.emotion != "Neutral") "[${line.emotion.lowercase()}] ${line.text}" else line.text

                val ok = audioEngine.synthesizeTextToFile(textWithEmotion, voice, lineFile)
                if (ok && lineFile.exists()) {
                    renderedParts.add(lineFile)
                }
            }

            _processingStatusText.value = "Mixing master audio & background ambiance..."
            _processingProgress.value = 0.9f

            val vocalMasterFile = File(tempDir, "vocals_master.wav")
            DspAudioProcessor.stitchWavFiles(renderedParts, vocalMasterFile, silencePauseMs = 450)

            val finalStoryFile = File(context.filesDir, "story_${System.currentTimeMillis()}.wav")

            // If background ambience selected, synthesize and mix
            val ambienceType = _selectedAmbience.value
            if (ambienceType != null && vocalMasterFile.exists()) {
                val vocalWav = DspAudioProcessor.readWavFile(vocalMasterFile)
                if (vocalWav != null) {
                    val sfxSamples = DspAudioProcessor.synthesizeSoundEffect(ambienceType, vocalWav.sampleRate)
                    val mixed = DspAudioProcessor.mixTracks(vocalWav.samples, 1.0f, sfxSamples, 0.22f)
                    DspAudioProcessor.writeWavFile(finalStoryFile, vocalWav.sampleRate, 1, mixed)
                } else {
                    vocalMasterFile.copyTo(finalStoryFile, overwrite = true)
                }
            } else {
                vocalMasterFile.copyTo(finalStoryFile, overwrite = true)
            }

            tempDir.deleteRecursively()
            _isProcessingJob.value = false

            if (finalStoryFile.exists()) {
                val wav = DspAudioProcessor.readWavFile(finalStoryFile)
                val durationMs = if (wav != null && wav.sampleRate > 0) {
                    (wav.samples.size.toDouble() / wav.sampleRate * 1000.0).toLong()
                } else 10000L

                val project = AudioProject(
                    title = title,
                    scriptText = lines.joinToString("\n") { "${it.speaker}: ${it.text}" },
                    voiceId = "multi_cast",
                    voiceName = "Full Cast Ensemble",
                    audioFilePath = finalStoryFile.absolutePath,
                    durationMs = durationMs,
                    wordCount = lines.sumOf { it.text.split(" ").size },
                    workspaceType = "Story",
                    fileSizeBytes = finalStoryFile.length()
                )
                db.projectDao().insertProject(project)

                _activeAudioFile.value = finalStoryFile
                _statusMessage.value = "Story master assembled! Playing..."
                audioEngine.playAudio(finalStoryFile.absolutePath)
            }
        }
    }

    // VOICE CLONING STUDIO
    fun saveClonedVoice(
        name: String,
        gender: String,
        samplePath: String,
        consentConfirmed: Boolean,
        description: String
    ) {
        if (!consentConfirmed) {
            _statusMessage.value = "Consent verification is required to create a voice profile."
            return
        }
        if (name.isBlank() || samplePath.isBlank()) {
            _statusMessage.value = "Please record or upload a sample and provide a voice name."
            return
        }

        viewModelScope.launch {
            val sampleFile = File(samplePath)
            val wav = DspAudioProcessor.readWavFile(sampleFile)
            val durationSec = if (wav != null) (wav.samples.size.toFloat() / wav.sampleRate) else 5.0f

            val clone = ClonedVoice(
                name = name,
                gender = gender,
                baseSamplePath = samplePath,
                estimatedPitchF0 = if (gender.equals("female", true)) 210f else 125f,
                sampleDurationSec = durationSec,
                qualityScore = 96,
                description = description.ifBlank { "Custom cloned authorized voice profile." }
            )
            db.clonedVoiceDao().insertClone(clone)
            _statusMessage.value = "Voice profile '${name}' calibrated and saved successfully!"
        }
    }

    // VOICE CHANGER DSP
    fun applyVoiceChanger(effect: DspAudioProcessor.VoiceChangerEffect) {
        val input = _activeAudioFile.value
        if (input == null || !input.exists()) {
            _statusMessage.value = "Please load or generate an audio sample first."
            return
        }

        viewModelScope.launch {
            _isProcessingJob.value = true
            _processingStatusText.value = "Transforming voice with ${effect.displayName} DSP..."

            val wav = DspAudioProcessor.readWavFile(input)
            if (wav != null) {
                val changedSamples = DspAudioProcessor.applyVoiceChanger(wav, effect)
                val changedFile = File(context.filesDir, "changed_${System.currentTimeMillis()}.wav")
                DspAudioProcessor.writeWavFile(changedFile, wav.sampleRate, 1, changedSamples)

                _processedAudioFile.value = changedFile
                _statusMessage.value = "Applied ${effect.displayName}! Playing transformed audio."
                audioEngine.playAudio(changedFile.absolutePath)
            } else {
                _statusMessage.value = "Could not parse audio format for transformation."
            }
            _isProcessingJob.value = false
        }
    }

    // NOISE REMOVAL DSP
    fun applyNoiseRemoval(strength: Float, removeHum: Boolean, removeHiss: Boolean) {
        val input = _activeAudioFile.value
        if (input == null || !input.exists()) {
            _statusMessage.value = "Please load an audio recording first."
            return
        }

        viewModelScope.launch {
            _isProcessingJob.value = true
            _processingStatusText.value = "Running spectral noise gate & notch filters..."

            val wav = DspAudioProcessor.readWavFile(input)
            if (wav != null) {
                val cleaned = DspAudioProcessor.applyNoiseReduction(wav, strength, removeHum, removeHiss)
                val cleanFile = File(context.filesDir, "denoised_${System.currentTimeMillis()}.wav")
                DspAudioProcessor.writeWavFile(cleanFile, wav.sampleRate, 1, cleaned)

                _processedAudioFile.value = cleanFile
                _statusMessage.value = "Noise reduction complete! Comparing audio."
                audioEngine.playAudio(cleanFile.absolutePath)
            }
            _isProcessingJob.value = false
        }
    }

    // VOICE ENHANCEMENT DSP
    fun applyVoiceEnhancement(
        bassBoostDb: Float,
        trebleDb: Float,
        compressionRatio: Float,
        normalize: Boolean,
        reverbAmount: Float
    ) {
        val input = _activeAudioFile.value
        if (input == null || !input.exists()) {
            _statusMessage.value = "Please load an audio recording first."
            return
        }

        viewModelScope.launch {
            _isProcessingJob.value = true
            _processingStatusText.value = "Mastering vocal harmonics, EQ, and dynamics..."

            val wav = DspAudioProcessor.readWavFile(input)
            if (wav != null) {
                val enhanced = DspAudioProcessor.applyVoiceEnhancement(
                    wav, bassBoostDb, trebleDb, compressionRatio, normalize, reverbAmount
                )
                val enhFile = File(context.filesDir, "enhanced_${System.currentTimeMillis()}.wav")
                DspAudioProcessor.writeWavFile(enhFile, wav.sampleRate, 1, enhanced)

                _processedAudioFile.value = enhFile
                _statusMessage.value = "Voice enhancement applied! Playing master."
                audioEngine.playAudio(enhFile.absolutePath)
            }
            _isProcessingJob.value = false
        }
    }

    // AUDIO EDITOR: TRIM & GAIN
    fun applyAudioEdit(startMs: Long, endMs: Long, gain: Float, fadeInMs: Long, fadeOutMs: Long) {
        val input = _activeAudioFile.value
        if (input == null || !input.exists()) {
            _statusMessage.value = "Please load an audio recording first."
            return
        }

        viewModelScope.launch {
            _isProcessingJob.value = true
            _processingStatusText.value = "Trimming and adjusting audio waveform..."

            val wav = DspAudioProcessor.readWavFile(input)
            if (wav != null) {
                val edited = DspAudioProcessor.editAudio(wav, startMs, endMs, gain, fadeInMs, fadeOutMs)
                val editFile = File(context.filesDir, "edited_${System.currentTimeMillis()}.wav")
                DspAudioProcessor.writeWavFile(editFile, wav.sampleRate, 1, edited)

                _activeAudioFile.value = editFile
                _statusMessage.value = "Audio edited successfully!"
                audioEngine.playAudio(editFile.absolutePath)
            }
            _isProcessingJob.value = false
        }
    }

    // SOUND EFFECTS (SFX) SYNTHESIS
    fun generateSoundEffect(type: DspAudioProcessor.SfxType) {
        viewModelScope.launch {
            _isProcessingJob.value = true
            _processingStatusText.value = "Synthesizing ${type.sfxName} Foley audio..."

            val samples = DspAudioProcessor.synthesizeSoundEffect(type)
            val sfxFile = File(context.filesDir, "sfx_${type.name.lowercase()}_${System.currentTimeMillis()}.wav")
            DspAudioProcessor.writeWavFile(sfxFile, 44100, 1, samples)

            val project = AudioProject(
                title = "SFX: ${type.sfxName}",
                scriptText = "Synthesized Foley effect: ${type.sfxName} (${type.category})",
                voiceId = "sfx_generator",
                voiceName = "Foley Synth",
                audioFilePath = sfxFile.absolutePath,
                durationMs = (type.durationSec * 1000).toLong(),
                wordCount = 0,
                workspaceType = "SFX",
                fileSizeBytes = sfxFile.length()
            )
            db.projectDao().insertProject(project)

            _activeAudioFile.value = sfxFile
            _isProcessingJob.value = false
            _statusMessage.value = "Synthesized ${type.sfxName}! Playing Foley audio."
            audioEngine.playAudio(sfxFile.absolutePath)
        }
    }

    // DUBBING & TRANSLATION
    fun dubAndTranslate(text: String, targetLanguage: String, voice: VoiceProfile) {
        viewModelScope.launch {
            _isProcessingJob.value = true
            _processingStatusText.value = "Translating script for $targetLanguage..."
            val translated = geminiService.translateText(text, targetLanguage)

            _processingStatusText.value = "Synthesizing dubbed speech in $targetLanguage..."
            val dubFile = File(context.filesDir, "dub_${System.currentTimeMillis()}.wav")
            val success = audioEngine.synthesizeTextToFile(translated, voice, dubFile)

            _isProcessingJob.value = false
            if (success && dubFile.exists()) {
                val words = translated.split(" ").size
                val project = AudioProject(
                    title = "Dubbed: $targetLanguage Production",
                    scriptText = "[$targetLanguage] $translated\n\n[Original]: $text",
                    voiceId = voice.id,
                    voiceName = voice.name,
                    audioFilePath = dubFile.absolutePath,
                    durationMs = 4000L,
                    wordCount = words,
                    workspaceType = "Dubbing",
                    fileSizeBytes = dubFile.length()
                )
                db.projectDao().insertProject(project)

                _activeAudioFile.value = dubFile
                _statusMessage.value = "Dubbing generated! Playing audio."
                audioEngine.playAudio(dubFile.absolutePath)
            }
        }
    }

    // PRONUNCIATION DICTIONARY
    fun addPronunciationRule(word: String, phonetic: String, isCaseSensitive: Boolean) {
        if (word.isBlank() || phonetic.isBlank()) return
        viewModelScope.launch {
            val rule = PronunciationRule(
                wordToReplace = word.trim(),
                phoneticReplacement = phonetic.trim(),
                isCaseSensitive = isCaseSensitive
            )
            db.pronunciationDao().insertRule(rule)
            _statusMessage.value = "Pronunciation rule added: '$word' -> '$phonetic'"
        }
    }

    fun deletePronunciationRule(rule: PronunciationRule) {
        viewModelScope.launch {
            db.pronunciationDao().deleteRule(rule)
        }
    }

    // PROJECT & EXPORT MANAGEMENT
    fun deleteProject(project: AudioProject) {
        viewModelScope.launch {
            try {
                File(project.audioFilePath).delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            db.projectDao().deleteProject(project)
            _statusMessage.value = "Project deleted."
        }
    }

    fun shareAudio(filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            _statusMessage.value = "Audio file not found."
            return
        }

        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(intent, "Export / Share Audio").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            // Fallback direct intent
            try {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "audio/*"
                    putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(intent, "Share Audio").apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
            } catch (err: Exception) {
                err.printStackTrace()
                _statusMessage.value = "Export ready at: ${file.name} (${file.length() / 1024} KB)"
            }
        }
    }

    fun downloadAudio(file: File, preferredTitle: String = "VocalForge_Audio") {
        if (file.exists()) {
            val result = com.example.audio.AudioDownloader.downloadToDevice(context, file, preferredTitle)
            result.onSuccess { msg ->
                _statusMessage.value = msg
            }.onFailure { err ->
                _statusMessage.value = "Download failed: ${err.message}"
            }
        } else {
            _statusMessage.value = "No audio file available to download."
        }
    }

    fun setActiveFile(file: File) {
        _activeAudioFile.value = file
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
