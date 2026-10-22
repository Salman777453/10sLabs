package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class StudioWorkspace(val title: String, val iconName: String, val category: String) {
    TTS_STUDIO("Text to Speech", "Mic", "Creation"),
    VOICE_LIBRARY("Voice Library (200+)", "RecordVoiceOver", "Creation"),
    STORY_STUDIO("Multi-Character Studio", "AutoStories", "Creation"),
    VOICE_CLONING("Voice Cloning", "Fingerprint", "Cloning & FX"),
    VOICE_CHANGER("Voice Changer", "GraphicEq", "Cloning & FX"),
    VOICE_RECORDER("Voice Recorder", "RadioButtonChecked", "Audio Lab"),
    NOISE_REMOVAL("Noise Removal", "HearingDisabled", "Audio Lab"),
    VOICE_ENHANCE("Voice Enhancement", "Tune", "Audio Lab"),
    AUDIO_EDITOR("Editor & Mixer", "VolumeUp", "Audio Lab"),
    SPEECH_TO_TEXT("Speech to Text", "Subtitles", "Tools"),
    DUBBING("Dubbing & Translation", "Translate", "Tools"),
    SOUND_EFFECTS("Sound Effects (SFX)", "SurroundSound", "Tools"),
    PRONUNCIATION("Pronunciation Dict", "Spellcheck", "Tools"),
    PROJECTS_HISTORY("Projects & History", "Folder", "Management"),
    EXPORT_CENTER("Export & Downloads", "Download", "Management"),
    SETTINGS("Settings & Privacy", "Settings", "Management")
}

data class VoiceProfile(
    val id: String,
    val name: String,
    val gender: String, // "Male", "Female", "Youth", "Character"
    val ageGroup: String, // "Youth", "Adult", "Mature", "Elder"
    val accent: String, // "US", "UK", "Australia", "India", "Ireland", "Global", etc.
    val languageCode: String = "en-US",
    val styleCategory: String, // "Narration", "Conversational", "News", "Commercial", "Cartoon", "Deep Bass", "Soft Whisper", "Fantasy", "Meditation", "Podcast"
    val toneDescription: String,
    val sampleRateHz: Int = 44100,
    val pitchMultiplier: Float = 1.0f,
    val speedMultiplier: Float = 1.0f,
    val isCustomClone: Boolean = false,
    val isFavorite: Boolean = false
)

@Entity(tableName = "projects")
data class AudioProject(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val scriptText: String,
    val voiceId: String,
    val voiceName: String,
    val audioFilePath: String,
    val durationMs: Long,
    val wordCount: Int,
    val workspaceType: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val sampleRate: Int = 44100,
    val fileSizeBytes: Long = 0L
)

@Entity(tableName = "pronunciation_rules")
data class PronunciationRule(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val wordToReplace: String,
    val phoneticReplacement: String,
    val isCaseSensitive: Boolean = false,
    val isEnabled: Boolean = true
)

@Entity(tableName = "cloned_voices")
data class ClonedVoice(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val gender: String,
    val baseSamplePath: String,
    val estimatedPitchF0: Float,
    val sampleDurationSec: Float,
    val qualityScore: Int,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class DialogueLine(
    val id: String = UUID.randomUUID().toString(),
    var speaker: String,
    var text: String,
    var voiceId: String,
    var emotion: String = "Neutral",
    var pitch: Float = 1.0f,
    var speed: Float = 1.0f,
    var audioPath: String? = null,
    var isGenerating: Boolean = false
)

data class SoundEffectItem(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val durationSec: Float,
    val iconName: String
)
