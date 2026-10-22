package com.example.audio

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.model.VoiceProfile
import com.example.data.voices.VoiceCatalog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.UUID
import kotlin.math.sqrt

class AudioEngine(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Player State
    private var mediaPlayer: MediaPlayer? = null
    private val _currentPlayingPath = MutableStateFlow<String?>(null)
    val currentPlayingPath = _currentPlayingPath.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress = _playbackProgress.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs = _durationMs.asStateFlow()

    // Recorder State
    private var isRecording = false
    private var recordingThread: Thread? = null
    private val _recordingDecibels = MutableStateFlow(0f)
    val recordingDecibels = _recordingDecibels.asStateFlow()

    private val _isRecordingActive = MutableStateFlow(false)
    val isRecordingActive = _isRecordingActive.asStateFlow()

    private val _recordedSeconds = MutableStateFlow(0)
    val recordedSeconds = _recordedSeconds.asStateFlow()

    init {
        initializeTts()
        startPlaybackMonitor()
    }

    private fun initializeTts() {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialized = true
                tts?.language = Locale.US
            }
        }
    }

    // Playback control
    fun playAudio(filePath: String, onCompletion: (() -> Unit)? = null) {
        val file = File(filePath)
        if (!file.exists()) return

        stopAudio()

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                start()
                _currentPlayingPath.value = filePath
                _isPlaying.value = true
                _durationMs.value = duration.toLong()

                setOnCompletionListener {
                    _isPlaying.value = false
                    _playbackProgress.value = 0f
                    _currentPositionMs.value = 0L
                    onCompletion?.invoke()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
        }
    }

    fun pauseAudio() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            }
        }
    }

    fun resumeAudio() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _isPlaying.value = true
            }
        }
    }

    fun togglePlayback(filePath: String) {
        if (_currentPlayingPath.value == filePath && mediaPlayer != null) {
            if (mediaPlayer?.isPlaying == true) {
                pauseAudio()
            } else {
                resumeAudio()
            }
        } else {
            playAudio(filePath)
        }
    }

    fun seekToFraction(fraction: Float) {
        mediaPlayer?.let {
            val targetMs = (fraction * it.duration).toInt()
            it.seekTo(targetMs)
            _playbackProgress.value = fraction
            _currentPositionMs.value = targetMs.toLong()
        }
    }

    fun stopAudio() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
            _isPlaying.value = false
            _playbackProgress.value = 0f
            _currentPositionMs.value = 0L
        }
    }

    private fun startPlaybackMonitor() {
        scope.launch {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying && mp.duration > 0) {
                        val current = mp.currentPosition
                        val total = mp.duration
                        _currentPositionMs.value = current.toLong()
                        _durationMs.value = total.toLong()
                        _playbackProgress.value = (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                    }
                }
                delay(100)
            }
        }
    }

    // Text-to-Speech Single Chunk Synthesis to WAV File
    suspend fun synthesizeTextToFile(
        text: String,
        voice: VoiceProfile,
        outputFile: File,
        pitchOffset: Float = 0f,
        speedOffset: Float = 0f
    ): Boolean = withContext(Dispatchers.IO) {
        val currentTts = tts ?: return@withContext false
        if (!isTtsInitialized) {
            delay(500)
        }

        // Apply script direction emotion syntax (e.g. [whispering], [excited], [sad], [angry])
        val (cleanedText, emotionPitch, emotionSpeed) = parseEmotionTags(text)

        // Set locale
        val locale = getLocaleFromCode(voice.languageCode)
        currentTts.language = locale

        // Base voice pitch and speed with offsets
        val finalPitch = (voice.pitchMultiplier + pitchOffset + emotionPitch).coerceIn(0.5f, 2.0f)
        val finalSpeed = (voice.speedMultiplier + speedOffset + emotionSpeed).coerceIn(0.5f, 2.0f)

        currentTts.setPitch(finalPitch)
        currentTts.setSpeechRate(finalSpeed)

        val utteranceId = UUID.randomUUID().toString()
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)

        val completer = CompletableDeferred<Boolean>()

        currentTts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String?) {}

            override fun onDone(id: String?) {
                if (id == utteranceId) {
                    completer.complete(true)
                }
            }

            override fun onError(id: String?) {
                if (id == utteranceId) {
                    completer.complete(false)
                }
            }
        })

        val result = currentTts.synthesizeToFile(cleanedText, params, outputFile, utteranceId)
        if (result != TextToSpeech.SUCCESS) {
            return@withContext false
        }

        try {
            withTimeout(15000) {
                completer.await()
            }
        } catch (e: Exception) {
            completer.complete(false)
            false
        }
    }

    // Parse script directions: [whispering], [excited], [sad], [angry], [calm], [laughing]
    private fun parseEmotionTags(text: String): Triple<String, Float, Float> {
        var clean = text
        var pitchDelta = 0f
        var speedDelta = 0f

        val lower = text.lowercase()
        if (lower.contains("[whispering]")) {
            pitchDelta = -0.1f
            speedDelta = -0.15f
            clean = clean.replace(Regex("\\[whispering\\]", RegexOption.IGNORE_CASE), "")
        } else if (lower.contains("[excited]")) {
            pitchDelta = 0.15f
            speedDelta = 0.15f
            clean = clean.replace(Regex("\\[excited\\]", RegexOption.IGNORE_CASE), "")
        } else if (lower.contains("[sad]")) {
            pitchDelta = -0.15f
            speedDelta = -0.2f
            clean = clean.replace(Regex("\\[sad\\]", RegexOption.IGNORE_CASE), "")
        } else if (lower.contains("[angry]")) {
            pitchDelta = 0.1f
            speedDelta = 0.1f
            clean = clean.replace(Regex("\\[angry\\]", RegexOption.IGNORE_CASE), "")
        } else if (lower.contains("[calm]")) {
            pitchDelta = -0.05f
            speedDelta = -0.1f
            clean = clean.replace(Regex("\\[calm\\]", RegexOption.IGNORE_CASE), "")
        } else if (lower.contains("[dramatic pause]")) {
            clean = clean.replace(Regex("\\[dramatic pause\\]", RegexOption.IGNORE_CASE), "... ...")
        }

        return Triple(clean.trim(), pitchDelta, speedDelta)
    }

    private fun getLocaleFromCode(code: String): Locale {
        return when {
            code.startsWith("hi") -> Locale("hi", "IN")
            code.startsWith("ur") -> Locale("ur", "PK")
            code.startsWith("es") -> Locale("es", "ES")
            code.startsWith("fr") -> Locale.FRANCE
            code.startsWith("de") -> Locale.GERMANY
            code.startsWith("ja") -> Locale.JAPAN
            code.startsWith("it") -> Locale.ITALY
            code.startsWith("zh") -> Locale.SIMPLIFIED_CHINESE
            code.contains("GB") -> Locale.UK
            code.contains("AU") -> Locale("en", "AU")
            code.contains("CA") -> Locale.CANADA
            code.contains("IE") -> Locale("en", "IE")
            code.contains("IN") -> Locale("en", "IN")
            else -> Locale.US
        }
    }

    // Large Script Processing: 20,000–30,000 words Chunking Engine
    fun splitIntoSentenceChunks(fullText: String, maxChunkChars: Int = 450): List<String> {
        val paragraphs = fullText.split("\n\n")
        val chunks = mutableListOf<String>()

        for (para in paragraphs) {
            val trimmedPara = para.trim()
            if (trimmedPara.isEmpty()) continue

            if (trimmedPara.length <= maxChunkChars) {
                chunks.add(trimmedPara)
            } else {
                // Split by sentence boundaries (. ! ?)
                val sentences = trimmedPara.split(Regex("(?<=[.!?])\\s+"))
                var currentChunk = StringBuilder()

                for (sentence in sentences) {
                    if (currentChunk.length + sentence.length + 1 <= maxChunkChars) {
                        if (currentChunk.isNotEmpty()) currentChunk.append(" ")
                        currentChunk.append(sentence)
                    } else {
                        if (currentChunk.isNotEmpty()) {
                            chunks.add(currentChunk.toString())
                            currentChunk = StringBuilder()
                        }
                        if (sentence.length > maxChunkChars) {
                            // Break extremely long sentence by comma or words
                            val words = sentence.split(" ")
                            var subChunk = StringBuilder()
                            for (w in words) {
                                if (subChunk.length + w.length + 1 <= maxChunkChars) {
                                    if (subChunk.isNotEmpty()) subChunk.append(" ")
                                    subChunk.append(w)
                                } else {
                                    chunks.add(subChunk.toString())
                                    subChunk = StringBuilder(w)
                                }
                            }
                            if (subChunk.isNotEmpty()) {
                                chunks.add(subChunk.toString())
                            }
                        } else {
                            currentChunk.append(sentence)
                        }
                    }
                }
                if (currentChunk.isNotEmpty()) {
                    chunks.add(currentChunk.toString())
                }
            }
        }

        return if (chunks.isEmpty() && fullText.isNotBlank()) listOf(fullText) else chunks
    }

    // Synthesize long script with progress and resume support
    suspend fun processLargeScript(
        fullText: String,
        voice: VoiceProfile,
        outputFinalFile: File,
        onProgress: (currentChunk: Int, totalChunks: Int, statusMessage: String) -> Unit
    ): Boolean = withContext(Dispatchers.IO) {
        val chunks = splitIntoSentenceChunks(fullText)
        if (chunks.isEmpty()) return@withContext false

        val tempDir = File(context.cacheDir, "script_chunks_${System.currentTimeMillis()}")
        tempDir.mkdirs()

        val generatedFiles = mutableListOf<File>()

        for ((index, chunk) in chunks.withIndex()) {
            val chunkFile = File(tempDir, "chunk_$index.wav")
            onProgress(index + 1, chunks.size, "Synthesizing section ${index + 1} of ${chunks.size}...")

            val success = synthesizeTextToFile(chunk, voice, chunkFile)
            if (success && chunkFile.exists() && chunkFile.length() > 44) {
                generatedFiles.add(chunkFile)
            } else {
                // Retry once
                delay(200)
                val retrySuccess = synthesizeTextToFile(chunk, voice, chunkFile)
                if (retrySuccess && chunkFile.exists()) {
                    generatedFiles.add(chunkFile)
                }
            }
        }

        onProgress(chunks.size, chunks.size, "Stitching ${generatedFiles.size} audio segments...")
        val stitched = DspAudioProcessor.stitchWavFiles(generatedFiles, outputFinalFile, silencePauseMs = 300)

        // Cleanup temporary chunk files
        tempDir.deleteRecursively()

        stitched
    }

    // Real Microphone Voice Recording
    fun startMicrophoneRecording(outputWavFile: File): Boolean {
        if (isRecording) return false

        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat).coerceAtLeast(4096)

        val record: AudioRecord = try {
            AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        if (record.state != AudioRecord.STATE_INITIALIZED) {
            record.release()
            return false
        }

        isRecording = true
        _isRecordingActive.value = true
        _recordedSeconds.value = 0

        recordingThread = Thread {
            record.startRecording()
            val rawPcmFile = File(context.cacheDir, "temp_rec.raw")
            val pcmOut = FileOutputStream(rawPcmFile)
            val audioBuffer = ShortArray(bufferSize / 2)
            var totalSamples = 0L
            val startTime = System.currentTimeMillis()

            while (isRecording) {
                val readShorts = record.read(audioBuffer, 0, audioBuffer.size)
                if (readShorts > 0) {
                    val byteBuffer = java.nio.ByteBuffer.allocate(readShorts * 2).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                    var sumSquares = 0.0
                    for (i in 0 until readShorts) {
                        val s = audioBuffer[i]
                        byteBuffer.putShort(s)
                        sumSquares += (s * s)
                    }
                    pcmOut.write(byteBuffer.array())
                    totalSamples += readShorts

                    // Compute RMS decibels
                    val rms = sqrt(sumSquares / readShorts)
                    val db = (20.0 * kotlin.math.log10(rms.coerceAtLeast(1.0) / 32767.0)).toFloat().coerceIn(-60f, 0f)
                    _recordingDecibels.value = db

                    _recordedSeconds.value = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                }
            }

            try {
                record.stop()
                record.release()
                pcmOut.close()

                // Convert raw PCM to WAV
                val rawBytes = rawPcmFile.readBytes()
                val shortsCount = rawBytes.size / 2
                val samples = ShortArray(shortsCount)
                val bb = java.nio.ByteBuffer.wrap(rawBytes).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                for (i in 0 until shortsCount) {
                    samples[i] = bb.short
                }
                DspAudioProcessor.writeWavFile(outputWavFile, sampleRate, 1, samples)
                rawPcmFile.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.also { it.start() }

        return true
    }

    fun stopMicrophoneRecording() {
        isRecording = false
        _isRecordingActive.value = false
        recordingThread = null
    }

    fun release() {
        stopAudio()
        stopMicrophoneRecording()
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
