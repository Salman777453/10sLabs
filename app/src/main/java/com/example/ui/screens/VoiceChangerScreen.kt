package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.audio.DspAudioProcessor
import com.example.ui.MainViewModel
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.*

@Composable
fun VoiceChangerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeAudioFile by viewModel.activeAudioFile.collectAsState()
    val processedAudioFile by viewModel.processedAudioFile.collectAsState()
    val isProcessing by viewModel.isProcessingJob.collectAsState()
    val playbackProgress by viewModel.audioEngine.playbackProgress.collectAsState()
    val isPlaying by viewModel.audioEngine.isPlaying.collectAsState()

    var selectedEffect by remember { mutableStateOf(DspAudioProcessor.VoiceChangerEffect.TITAN_BASS) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("voice_changer_screen")
    ) {
        Text(
            text = "Voice Changer & Speech-to-Speech",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Real DSP time-domain pitch shift & formant transformation",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Input Audio Source Status
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
                        imageVector = if (activeAudioFile != null) Icons.Default.Audiotrack else Icons.Default.Warning,
                        contentDescription = "Source",
                        tint = if (activeAudioFile != null) NeonCyan else StudioAmber
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (activeAudioFile != null) "Source Audio Loaded" else "No Audio File Loaded",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = activeAudioFile?.name ?: "Generate speech in TTS Studio or record first",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                if (activeAudioFile != null) {
                    IconButton(onClick = { viewModel.audioEngine.playAudio(activeAudioFile!!.absolutePath) }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play Original", tint = NeonCyan)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Select Transformation Preset:",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Grid of 8 DSP Effects
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(DspAudioProcessor.VoiceChangerEffect.values()) { effect ->
                val isSel = effect == selectedEffect
                Card(
                    onClick = {
                        selectedEffect = effect
                        viewModel.applyVoiceChanger(effect)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSel) StudioCardHover else StudioCard
                    ),
                    border = BorderStroke(1.dp, if (isSel) NeonCyan else StudioBorder),
                    modifier = Modifier.testTag("changer_preset_${effect.name.lowercase()}")
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = effect.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) NeonCyan else TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = effect.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Waveform Visualizer
        if (processedAudioFile != null && processedAudioFile!!.exists()) {
            Text(
                text = "Transformed Result (${selectedEffect.displayName}):",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            WaveformVisualizer(
                progress = playbackProgress,
                isPlaying = isPlaying,
                activeColor = NeonPurple,
                onSeek = { viewModel.audioEngine.seekToFraction(it) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.applyVoiceChanger(selectedEffect) },
                enabled = !isProcessing && activeAudioFile != null,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("apply_voice_changer_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioDarkBg)
            ) {
                Icon(Icons.Default.GraphicEq, contentDescription = "Transform")
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isProcessing) "Transforming..." else "Transform Voice", fontWeight = FontWeight.Bold)
            }

            if (processedAudioFile != null) {
                IconButton(
                    onClick = { viewModel.shareAudio(processedAudioFile!!.absolutePath) },
                    modifier = Modifier
                        .size(50.dp)
                        .background(StudioCard, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = NeonCyan)
                }
            }
        }
    }
}
