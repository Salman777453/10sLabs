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
import com.example.ui.components.formatDuration
import com.example.ui.theme.*

@Composable
fun AudioEditorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeAudioFile by viewModel.activeAudioFile.collectAsState()
    val isProcessing by viewModel.isProcessingJob.collectAsState()
    val playbackProgress by viewModel.audioEngine.playbackProgress.collectAsState()
    val isPlaying by viewModel.audioEngine.isPlaying.collectAsState()
    val durationMs by viewModel.audioEngine.durationMs.collectAsState()

    var startMs by remember { mutableFloatStateOf(0f) }
    var endMs by remember { mutableFloatStateOf(5000f) }
    var volumeGain by remember { mutableFloatStateOf(1.0f) }
    var fadeInMs by remember { mutableFloatStateOf(0f) }
    var fadeOutMs by remember { mutableFloatStateOf(0f) }

    val safeDuration = remember(durationMs) { if (durationMs > 500) durationMs.toFloat() else 10000f }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("audio_editor_screen")
    ) {
        Text(
            text = "Audio Editor & Mixer",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Visual waveform trimming, volume automation, fades & master stem rendering",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Waveform Editor
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, StudioBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Timeline Waveform (${formatDuration(durationMs)})",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary
                    )
                    IconButton(
                        onClick = {
                            activeAudioFile?.let { viewModel.audioEngine.togglePlayback(it.absolutePath) }
                        }
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = "Toggle Play",
                            tint = NeonCyan,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                WaveformVisualizer(
                    progress = playbackProgress,
                    isPlaying = isPlaying,
                    onSeek = { viewModel.audioEngine.seekToFraction(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Trim Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, StudioBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Trim Boundaries (Crop Region):", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Start Offset", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(text = formatDuration(startMs.toLong()), style = MaterialTheme.typography.labelSmall, color = NeonCyan, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = startMs,
                    onValueChange = { startMs = it.coerceAtMost(endMs - 500f) },
                    valueRange = 0f..safeDuration,
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "End Offset", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(text = formatDuration(endMs.toLong()), style = MaterialTheme.typography.labelSmall, color = NeonPurple, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = endMs,
                    onValueChange = { endMs = it.coerceAtLeast(startMs + 500f) },
                    valueRange = 0f..safeDuration,
                    colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Level & Fades
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, StudioBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Master Output Gain", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                    Text(text = "${(volumeGain * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = StudioAmber, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = volumeGain,
                    onValueChange = { volumeGain = it },
                    valueRange = 0.2f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = StudioAmber, activeTrackColor = StudioAmber)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Fade In (${(fadeInMs).toInt()}ms)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Slider(value = fadeInMs, onValueChange = { fadeInMs = it }, valueRange = 0f..2000f)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Fade Out (${(fadeOutMs).toInt()}ms)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Slider(value = fadeOutMs, onValueChange = { fadeOutMs = it }, valueRange = 0f..2000f)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Render Action
        Button(
            onClick = {
                viewModel.applyAudioEdit(
                    startMs = startMs.toLong(),
                    endMs = endMs.toLong(),
                    gain = volumeGain,
                    fadeInMs = fadeInMs.toLong(),
                    fadeOutMs = fadeOutMs.toLong()
                )
            },
            enabled = !isProcessing && activeAudioFile != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("apply_audio_edit_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioDarkBg)
        ) {
            Icon(Icons.Default.ContentCut, contentDescription = "Trim")
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isProcessing) "Editing Audio..." else "Render & Save Edit", fontWeight = FontWeight.Bold)
        }
    }
}
