package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
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
import com.example.ui.theme.*

@Composable
fun NoiseRemovalScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeAudioFile by viewModel.activeAudioFile.collectAsState()
    val processedAudioFile by viewModel.processedAudioFile.collectAsState()
    val isProcessing by viewModel.isProcessingJob.collectAsState()
    val playbackProgress by viewModel.audioEngine.playbackProgress.collectAsState()
    val isPlaying by viewModel.audioEngine.isPlaying.collectAsState()

    var reductionStrength by remember { mutableFloatStateOf(0.65f) }
    var removeHum by remember { mutableStateOf(true) }
    var removeHiss by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("noise_removal_screen")
    ) {
        Text(
            text = "Noise Removal Studio",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Spectral noise gate, AC/fan hum elimination & hiss reduction",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Input Track Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, StudioBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (activeAudioFile != null) Icons.Default.HearingDisabled else Icons.Default.Warning,
                        contentDescription = "Status",
                        tint = if (activeAudioFile != null) NeonCyan else StudioAmber
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (activeAudioFile != null) "Audio Track Ready" else "No Input Audio Selected",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = activeAudioFile?.name ?: "Record audio or generate in TTS Studio first",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                if (activeAudioFile != null) {
                    IconButton(onClick = { viewModel.audioEngine.playAudio(activeAudioFile!!.absolutePath) }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play Input", tint = NeonCyan)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reduction Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, StudioBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Denoising Strength", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    Text(
                        text = "${(reductionStrength * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = reductionStrength,
                    onValueChange = { reductionStrength = it },
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "50/60Hz Electrical Hum Notch Filter", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                        Text(text = "Cuts low-frequency AC power line humming", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Switch(
                        checked = removeHum,
                        onCheckedChange = { removeHum = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp), color = StudioBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "High-Frequency Hiss & Fan Filter", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                        Text(text = "Eliminates room ambience and computer fan hiss", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Switch(
                        checked = removeHiss,
                        onCheckedChange = { removeHiss = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Waveforms for A/B Comparison
        if (processedAudioFile != null && processedAudioFile!!.exists()) {
            Text(
                text = "Clean Denoised Audio Waveform:",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            WaveformVisualizer(
                progress = playbackProgress,
                isPlaying = isPlaying,
                activeColor = StudioEmerald,
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
                onClick = { viewModel.applyNoiseRemoval(reductionStrength, removeHum, removeHiss) },
                enabled = !isProcessing && activeAudioFile != null,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("run_noise_removal_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioDarkBg)
            ) {
                Icon(Icons.Default.HearingDisabled, contentDescription = "Clean")
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isProcessing) "Cleaning Audio..." else "Remove Noise", fontWeight = FontWeight.Bold)
            }

            if (processedAudioFile != null) {
                OutlinedButton(
                    onClick = { viewModel.audioEngine.playAudio(processedAudioFile!!.absolutePath) },
                    modifier = Modifier.height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play Clean")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Play Clean")
                }
            }
        }
    }
}
