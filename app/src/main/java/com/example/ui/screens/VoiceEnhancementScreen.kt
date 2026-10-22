package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.MainViewModel
import com.example.ui.components.WaveformVisualizer
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

data class EnhancementPreset(
    val name: String,
    val bassDb: Float,
    val trebleDb: Float,
    val compression: Float,
    val reverb: Float
)

@Composable
fun VoiceEnhancementScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeAudioFile by viewModel.activeAudioFile.collectAsState()
    val processedAudioFile by viewModel.processedAudioFile.collectAsState()
    val isProcessing by viewModel.isProcessingJob.collectAsState()
    val playbackProgress by viewModel.audioEngine.playbackProgress.collectAsState()
    val isPlaying by viewModel.audioEngine.isPlaying.collectAsState()

    val presets = listOf(
        EnhancementPreset("Natural Voice", 0f, 0f, 1.2f, 0.0f),
        EnhancementPreset("Deep and Warm", 6f, 1f, 2.5f, 0.05f),
        EnhancementPreset("Clear and Bright", 1f, 6f, 2.0f, 0.02f),
        EnhancementPreset("Podcast Voice", 4f, 4f, 3.0f, 0.08f),
        EnhancementPreset("YouTube Narration", 3f, 5f, 2.8f, 0.04f),
        EnhancementPreset("Studio Voiceover", 2f, 3f, 3.5f, 0.0f),
        EnhancementPreset("Soft and Gentle", 1f, 2f, 1.5f, 0.12f),
        EnhancementPreset("Radio-Style", 5f, 2f, 4.0f, 0.03f)
    )

    var selectedPreset by remember { mutableStateOf(presets[3]) } // Podcast default
    var bassBoostDb by remember { mutableFloatStateOf(4f) }
    var trebleDb by remember { mutableFloatStateOf(4f) }
    var compressionRatio by remember { mutableFloatStateOf(2.5f) }
    var reverbAmount by remember { mutableFloatStateOf(0.05f) }
    var normalizeLoudness by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("voice_enhancement_screen")
    ) {
        Text(
            text = "Voice Enhancement Studio",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Mastering EQ, multiband warmth, dynamic compression & broadcast polish",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Preset Selector Row
        Text(text = "Studio Mastering Presets:", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(presets) { preset ->
                val isSel = preset.name == selectedPreset.name
                FilterChip(
                    selected = isSel,
                    onClick = {
                        selectedPreset = preset
                        bassBoostDb = preset.bassDb
                        trebleDb = preset.trebleDb
                        compressionRatio = preset.compression
                        reverbAmount = preset.reverb
                    },
                    label = { Text(preset.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonPurple,
                        selectedLabelColor = Color.White,
                        containerColor = StudioCard,
                        labelColor = TextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Manual Sliders Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, StudioBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Bass Warmth
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Bass Warmth & Resonance", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                    Text(text = "+${bassBoostDb.toInt()} dB", style = MaterialTheme.typography.labelSmall, color = NeonCyan, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = bassBoostDb,
                    onValueChange = { bassBoostDb = it },
                    valueRange = 0f..8f,
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                )

                // Treble Clarity
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Treble Air & Presence", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                    Text(text = "+${trebleDb.toInt()} dB", style = MaterialTheme.typography.labelSmall, color = NeonPurple, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = trebleDb,
                    onValueChange = { trebleDb = it },
                    valueRange = 0f..8f,
                    colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple)
                )

                // Dynamic Compression
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Dynamic Compression Ratio", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                    Text(text = String.format("%.1f:1", compressionRatio), style = MaterialTheme.typography.labelSmall, color = StudioAmber, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = compressionRatio,
                    onValueChange = { compressionRatio = it },
                    valueRange = 1.0f..4.0f,
                    colors = SliderDefaults.colors(thumbColor = StudioAmber, activeTrackColor = StudioAmber)
                )

                // Room Reverb
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Studio Ambience / Early Reflections", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                    Text(text = "${(reverbAmount * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
                Slider(
                    value = reverbAmount,
                    onValueChange = { reverbAmount = it },
                    valueRange = 0f..0.25f
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Loudness Normalization (-14 LUFS Target)", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                    Switch(
                        checked = normalizeLoudness,
                        onCheckedChange = { normalizeLoudness = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Waveform Output
        if (processedAudioFile != null && processedAudioFile!!.exists()) {
            Text(text = "Enhanced Master Output:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            WaveformVisualizer(
                progress = playbackProgress,
                isPlaying = isPlaying,
                activeColor = NeonPurple,
                onSeek = { viewModel.audioEngine.seekToFraction(it) }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    viewModel.applyVoiceEnhancement(
                        bassBoostDb = bassBoostDb,
                        trebleDb = trebleDb,
                        compressionRatio = compressionRatio,
                        normalize = normalizeLoudness,
                        reverbAmount = reverbAmount
                    )
                },
                enabled = !isProcessing && activeAudioFile != null,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("apply_enhancement_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Tune, contentDescription = "Enhance")
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isProcessing) "Mastering..." else "Apply Enhancement", fontWeight = FontWeight.Bold)
            }

            if (processedAudioFile != null) {
                OutlinedButton(
                    onClick = { viewModel.audioEngine.playAudio(processedAudioFile!!.absolutePath) },
                    modifier = Modifier.height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Play Master")
                }
            }
        }
    }
}
