package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEngine
import com.example.audio.DspAudioProcessor
import com.example.data.local.VocalForgeDatabase
import com.example.data.model.AudioProject
import com.example.data.model.PronunciationRule
import com.example.data.model.VoiceProfile
import com.example.data.voices.VoiceCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

sealed interface TtsGenerationStatus {
    object Idle : TtsGenerationStatus
    object Preparing : TtsGenerationStatus
    data class Generating(
        val progress: Float,
        val currentChunk: Int,
        val totalChunks: Int,
        val statusMessage: String
    ) : TtsGenerationStatus
    data class Success(
        val audioFile: File,
        val durationMs: Long,
        val wordCount: Int
    ) : TtsGenerationStatus
    data class Error(val message: String) : TtsGenerationStatus
}

class TextToSpeechViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication<Application>().applicationContext
    val audioEngine = AudioEngine(context)
    private val db = VocalForgeDatabase.getDatabase(context)

    // -------------------------------------------------------------
    // 1. Script Input State
    // -------------------------------------------------------------
    private val _scriptText = MutableStateFlow(
        "Welcome to VocalForge. Generate natural, expressive AI speech, create multi-character audiobooks, and craft professional studio audio with zero latency."
    )
    val scriptText = _scriptText.asStateFlow()

    private val _projectTitle = MutableStateFlow("Narration Episode 1")
    val projectTitle = _projectTitle.asStateFlow()

    val wordCount: StateFlow<Int> = _scriptText.map { text ->
        if (text.isBlank()) 0 else text.trim().split(Regex("\\s+")).size
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val charCount: StateFlow<Int> = _scriptText.map { text ->
        text.length
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // -------------------------------------------------------------
    // 2. Voice Selection & Parameters State
    // -------------------------------------------------------------
    private val _selectedVoice = MutableStateFlow(VoiceCatalog.allVoices.first())
    val selectedVoice = _selectedVoice.asStateFlow()

    private val _pitchOffset = MutableStateFlow(0f)
    val pitchOffset = _pitchOffset.asStateFlow()

    private val _speedOffset = MutableStateFlow(0f)
    val speedOffset = _speedOffset.asStateFlow()

    private val _stability = MutableStateFlow(0.75f)
    val stability = _stability.asStateFlow()

    private val _expressiveness = MutableStateFlow(0.85f)
    val expressiveness = _expressiveness.asStateFlow()

    private val _selectedFormat = MutableStateFlow("WAV (44.1kHz 16-bit)")
    val selectedFormat = _selectedFormat.asStateFlow()

    // -------------------------------------------------------------
    // 3. Generation Status State
    // -------------------------------------------------------------
    private val _generationStatus = MutableStateFlow<TtsGenerationStatus>(TtsGenerationStatus.Idle)
    val generationStatus = _generationStatus.asStateFlow()

    private var activeGenerationJob: Job? = null

    // Last generated audio file
    private val _lastGeneratedFile = MutableStateFlow<File?>(null)
    val lastGeneratedFile = _lastGeneratedFile.asStateFlow()

    // Pronunciation rules for automatic correction
    val activeRules: StateFlow<List<PronunciationRule>> = db.pronunciationDao().getAllRules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -------------------------------------------------------------
    // Actions: Script Input
    // -------------------------------------------------------------
    fun updateScriptText(newText: String) {
        _scriptText.value = newText
    }

    fun updateProjectTitle(newTitle: String) {
        _projectTitle.value = newTitle
    }

    fun insertDirectionTag(tag: String) {
        val current = _scriptText.value
        _scriptText.value = if (current.endsWith(" ") || current.isEmpty()) {
            "$current$tag "
        } else {
            "$current $tag "
        }
    }

    fun clearScript() {
        _scriptText.value = ""
    }

    fun loadSampleScript(category: String) {
        val sample = when (category) {
            "Documentary" -> "Narrator: Deep beneath the polar ice caps, ancient ocean currents shape the climate of our entire world. For millions of years, these frozen abysses remained untouched by human hands."
            "Audiobook" -> "The rain tapped against the high library windows like nervous fingertips. [whispering] 'Did you bring the key?' Elena asked, her breath misting in the cold air. [calm] 'Always,' Marcus replied."
            "Commercial" -> "[excited] Introducing the all-new ultra-quiet electric roadster. Precision engineered with pure acoustic silence and zero emissions. Visit your local showroom today!"
            "Podcast" -> "Hey everyone, welcome back to The Sound Lab! Today we are exploring the future of generative voice modeling, neural audio synthesis, and how creators are breaking boundaries."
            else -> "Welcome to VocalForge. Generate natural, expressive AI speech, create multi-character audiobooks, and craft professional studio audio."
        }
        _scriptText.value = sample
    }

    // -------------------------------------------------------------
    // Actions: Voice Selection
    // -------------------------------------------------------------
    fun selectVoice(voice: VoiceProfile) {
        _selectedVoice.value = voice
    }

    fun updatePitchOffset(value: Float) {
        _pitchOffset.value = value.coerceIn(-0.4f, 0.4f)
    }

    fun updateSpeedOffset(value: Float) {
        _speedOffset.value = value.coerceIn(-0.5f, 0.5f)
    }

    fun updateStability(value: Float) {
        _stability.value = value.coerceIn(0.0f, 1.0f)
    }

    fun updateExpressiveness(value: Float) {
        _expressiveness.value = value.coerceIn(0.0f, 1.0f)
    }

    fun updateFormat(format: String) {
        _selectedFormat.value = format
    }

    // -------------------------------------------------------------
    // Actions: Generation Workflow
    // -------------------------------------------------------------
    fun synthesizeSpeech(onSuccess: ((AudioProject) -> Unit)? = null) {
        val rawText = _scriptText.value.trim()
        if (rawText.isBlank()) {
            _generationStatus.value = TtsGenerationStatus.Error("Please enter script text to synthesize.")
            return
        }

        activeGenerationJob?.cancel()
        activeGenerationJob = viewModelScope.launch {
            _generationStatus.value = TtsGenerationStatus.Preparing

            // 1. Apply active Pronunciation Dictionary rules
            var preparedText = rawText
            val rules = activeRules.value
            for (rule in rules) {
                if (rule.isEnabled) {
                    preparedText = if (rule.isCaseSensitive) {
                        preparedText.replace(rule.wordToReplace, rule.phoneticReplacement)
                    } else {
                        preparedText.replace(
                            Regex(Regex.escape(rule.wordToReplace), RegexOption.IGNORE_CASE),
                            rule.phoneticReplacement
                        )
                    }
                }
            }

            val voice = _selectedVoice.value
            val outputFile = File(context.filesDir, "tts_${System.currentTimeMillis()}.wav")

            // 2. Check if long script (> 400 characters) to trigger auto-chunking
            val isLongScript = preparedText.length > 400

            val success = if (isLongScript) {
                audioEngine.processLargeScript(preparedText, voice, outputFile) { current, total, statusMsg ->
                    _generationStatus.value = TtsGenerationStatus.Generating(
                        progress = current.toFloat() / total.toFloat(),
                        currentChunk = current,
                        totalChunks = total,
                        statusMessage = statusMsg
                    )
                }
            } else {
                _generationStatus.value = TtsGenerationStatus.Generating(
                    progress = 0.5f,
                    currentChunk = 1,
                    totalChunks = 1,
                    statusMessage = "Synthesizing voice with neural engine..."
                )
                audioEngine.synthesizeTextToFile(
                    text = preparedText,
                    voice = voice,
                    outputFile = outputFile,
                    pitchOffset = _pitchOffset.value,
                    speedOffset = _speedOffset.value
                )
            }

            if (success && outputFile.exists() && outputFile.length() > 44) {
                val wav = DspAudioProcessor.readWavFile(outputFile)
                val durationMs = if (wav != null && wav.sampleRate > 0) {
                    (wav.samples.size.toDouble() / wav.sampleRate * 1000.0).toLong()
                } else 3000L

                val totalWords = preparedText.split(Regex("\\s+")).size
                val currentTitle = _projectTitle.value.ifBlank { "Speech (${voice.name})" }

                // Save project to SQLite Room DB
                val project = AudioProject(
                    title = currentTitle,
                    scriptText = rawText,
                    voiceId = voice.id,
                    voiceName = voice.name,
                    audioFilePath = outputFile.absolutePath,
                    durationMs = durationMs,
                    wordCount = totalWords,
                    workspaceType = "TTS",
                    fileSizeBytes = outputFile.length()
                )
                db.projectDao().insertProject(project)

                _lastGeneratedFile.value = outputFile
                _generationStatus.value = TtsGenerationStatus.Success(
                    audioFile = outputFile,
                    durationMs = durationMs,
                    wordCount = totalWords
                )

                onSuccess?.invoke(project)
                audioEngine.playAudio(outputFile.absolutePath)
            } else {
                _generationStatus.value = TtsGenerationStatus.Error(
                    "Speech synthesis could not be completed. Please check your text input."
                )
            }
        }
    }

    fun cancelGeneration() {
        activeGenerationJob?.cancel()
        _generationStatus.value = TtsGenerationStatus.Idle
    }

    fun auditionSelectedVoice() {
        val voice = _selectedVoice.value
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

    fun dismissError() {
        if (_generationStatus.value is TtsGenerationStatus.Error) {
            _generationStatus.value = TtsGenerationStatus.Idle
        }
    }

    fun downloadGeneratedAudio(targetFile: File? = _lastGeneratedFile.value) {
        val file = targetFile ?: run {
            val status = _generationStatus.value
            if (status is TtsGenerationStatus.Success) status.audioFile else null
        }
        if (file != null && file.exists()) {
            val title = _projectTitle.value.ifBlank { "VocalForge_Speech" }
            com.example.audio.AudioDownloader.downloadToDevice(context, file, title)
        } else {
            _generationStatus.value = TtsGenerationStatus.Error("No generated audio available to download. Please synthesize speech first.")
        }
    }

    fun shareGeneratedAudio(targetFile: File? = _lastGeneratedFile.value) {
        val file = targetFile ?: run {
            val status = _generationStatus.value
            if (status is TtsGenerationStatus.Success) status.audioFile else null
        }
        if (file != null && file.exists()) {
            com.example.audio.AudioDownloader.shareAudioFile(context, file, "Export Voice Audio")
        }
    }

    override fun onCleared() {
        super.onCleared()
        activeGenerationJob?.cancel()
        audioEngine.stopAudio()
    }
}
