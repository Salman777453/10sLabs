package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.audio.DspAudioProcessor
import com.example.data.voices.VoiceCatalog
import com.example.ui.TextToSpeechViewModel
import com.example.ui.TtsGenerationStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("VocalForge", appName)
  }

  @Test
  fun `verify voice catalog contains over 200 distinct voices`() {
    val voices = VoiceCatalog.allVoices
    assertTrue("Voice catalog should have at least 200 voices, actual: ${voices.size}", voices.size >= 200)

    // Verify distinct IDs
    val uniqueIds = voices.map { it.id }.toSet()
    assertEquals(voices.size, uniqueIds.size)

    // Verify presence of required languages: Hindi & Urdu
    val hasHindi = voices.any { it.languageCode.startsWith("hi") }
    val hasUrdu = voices.any { it.languageCode.startsWith("ur") }
    assertTrue("Should contain Hindi voice", hasHindi)
    assertTrue("Should contain Urdu voice", hasUrdu)
  }

  @Test
  fun `verify dsp sound effect synthesis produces valid non-empty audio`() {
    val samples = DspAudioProcessor.synthesizeSoundEffect(DspAudioProcessor.SfxType.SCI_FI_LASER, sampleRate = 44100)
    assertTrue("Laser SFX should have audio samples", samples.isNotEmpty())
    val nonZeroCount = samples.count { it != 0.toShort() }
    assertTrue("Synthesized samples should contain active waveform", nonZeroCount > 100)
  }

  @Test
  fun `verify text to speech view model state management`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = TextToSpeechViewModel(app)

    // Check initial state
    assertEquals(TtsGenerationStatus.Idle, viewModel.generationStatus.value)

    // Test script text and word count
    viewModel.updateScriptText("The quick brown fox jumps over the lazy dog.")
    assertEquals("The quick brown fox jumps over the lazy dog.", viewModel.scriptText.value)
    assertEquals(9, viewModel.wordCount.value)
    assertEquals(44, viewModel.charCount.value)

    // Test direction tag insertion
    viewModel.insertDirectionTag("[whispering]")
    assertTrue(viewModel.scriptText.value.contains("[whispering]"))

    // Test voice selection
    val testVoice = VoiceCatalog.allVoices[5]
    viewModel.selectVoice(testVoice)
    assertEquals(testVoice.id, viewModel.selectedVoice.value.id)

    // Test pitch and speed offset updates
    viewModel.updatePitchOffset(0.2f)
    assertEquals(0.2f, viewModel.pitchOffset.value, 0.001f)

    viewModel.updateSpeedOffset(-0.1f)
    assertEquals(-0.1f, viewModel.speedOffset.value, 0.001f)
  }

  @Test
  fun `verify audio download helper saves file to downloads`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val tempAudio = java.io.File(context.cacheDir, "test_audio.wav")
    DspAudioProcessor.writeWavFile(tempAudio, 44100, 1, ShortArray(1000) { 500 })

    val result = com.example.audio.AudioDownloader.downloadToDevice(context, tempAudio, "TestSpeech")
    assertTrue("Download should succeed", result.isSuccess)
    val msg = result.getOrNull() ?: ""
    assertTrue("Result message should reference VocalForge or download: $msg", msg.contains("VocalForge") || msg.contains("Download") || msg.contains("Export"))
  }
}
