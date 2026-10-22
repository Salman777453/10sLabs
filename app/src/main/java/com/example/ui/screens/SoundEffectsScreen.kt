package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import com.example.ui.theme.*

@Composable
fun SoundEffectsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeAudioFile by viewModel.activeAudioFile.collectAsState()
    val isProcessing by viewModel.isProcessingJob.collectAsState()
    val playingPath by viewModel.audioEngine.currentPlayingPath.collectAsState()
    val isPlaying by viewModel.audioEngine.isPlaying.collectAsState()

    val sfxList = DspAudioProcessor.SfxType.values()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("sound_effects_screen")
    ) {
        Text(
            text = "Sound Effects (SFX) Studio",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Algorithmic mathematical Foley synthesis for productions & stories",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(sfxList) { sfx ->
                val isThisPlaying = isPlaying && playingPath?.contains(sfx.name.lowercase()) == true

                Card(
                    modifier = Modifier.testTag("sfx_card_${sfx.name.lowercase()}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, if (isThisPlaying) NeonCyan else StudioBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = StudioCard
                            ) {
                                Text(
                                    text = sfx.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonPurple,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "${sfx.durationSec}s",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = sfx.sfxName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.generateSoundEffect(sfx) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isThisPlaying) NeonCyan else StudioBorder,
                                contentColor = if (isThisPlaying) StudioDarkBg else TextPrimary
                            ),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isThisPlaying) Icons.Default.VolumeUp else Icons.Default.SurroundSound,
                                contentDescription = "Play SFX",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isThisPlaying) "Playing..." else "Synthesize",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
