package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.DspAudioProcessor
import com.example.data.voices.VoiceCatalog
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiCharacterStoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lines by viewModel.storyLines.collectAsState()
    val isProcessing by viewModel.isProcessingJob.collectAsState()
    val statusText by viewModel.processingStatusText.collectAsState()
    val progress by viewModel.processingProgress.collectAsState()
    val selectedAmbience by viewModel.selectedAmbience.collectAsState()

    var scriptText by remember {
        mutableStateOf(
            """
                Narrator: The ancient vault door opened with a slow, grinding whisper in the midnight fog.
                Elena (whispering): Did you hear that Marcus? We shouldn't be here.
                Marcus (calm): Stay close. The quantum relay is inside this chamber.
                AI Core: Warning. Biological intrusion detected on Sector Nine.
                Narrator (excited): Suddenly, crimson emergency runes ignited along the stone walls!
            """.trimIndent()
        )
    }

    var showScriptEditor by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("multi_character_story_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Story & Audiobook Studio",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Multi-cast dialogue production with background ambience",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            IconButton(
                onClick = { showScriptEditor = !showScriptEditor },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(StudioCard)
            ) {
                Icon(
                    imageVector = if (showScriptEditor) Icons.Default.Close else Icons.Default.Edit,
                    contentDescription = "Edit Script",
                    tint = NeonCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Script Editor Expandable Card
        if (showScriptEditor) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, NeonCyan)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Paste Screenplay / Dialogue Script:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = scriptText,
                        onValueChange = { scriptText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = StudioBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                viewModel.parseStoryScript(scriptText)
                                showScriptEditor = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioDarkBg)
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = "Parse")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Detect Speakers & Cast")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Background Ambience Track Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = StudioCard),
            border = BorderStroke(1.dp, StudioBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SurroundSound, contentDescription = "Ambience", tint = NeonPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Background Ambience Track", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                        Text(
                            text = selectedAmbience?.sfxName ?: "None (Clean Vocal Only)",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selectedAmbience != null) NeonCyan else TextMuted
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(
                        selected = selectedAmbience == null,
                        onClick = { viewModel.setAmbience(null) },
                        label = { Text("None") }
                    )
                    FilterChip(
                        selected = selectedAmbience == DspAudioProcessor.SfxType.RAINSTORM,
                        onClick = { viewModel.setAmbience(DspAudioProcessor.SfxType.RAINSTORM) },
                        label = { Text("Rain") }
                    )
                    FilterChip(
                        selected = selectedAmbience == DspAudioProcessor.SfxType.WIND_GUST,
                        onClick = { viewModel.setAmbience(DspAudioProcessor.SfxType.WIND_GUST) },
                        label = { Text("Wind") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress bar if assembling
        if (isProcessing) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StudioConsole),
                border = BorderStroke(1.dp, StudioAmber)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = statusText, style = MaterialTheme.typography.bodySmall, color = StudioAmber)
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = StudioAmber,
                        trackColor = StudioBorder
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Dialogue Turns Cast List
        Text(
            text = "Dialogue & Character Turns (${lines.size}):",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(lines, key = { it.id }) { line ->
                val voice = VoiceCatalog.getVoiceById(line.voiceId)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, StudioBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = line.speaker.take(1).uppercase(),
                                        color = StudioDarkBg,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = line.speaker,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = StudioBorder
                                ) {
                                    Text(
                                        text = line.emotion,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonPurple,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.synthesizeSingleStoryLine(line) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Test Line", tint = NeonCyan, modifier = Modifier.size(20.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "\"${line.text}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Cast Voice: ",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Text(
                                text = "${voice.name} (${voice.gender})",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Assemble Button
        Button(
            onClick = { viewModel.assembleCompleteStory() },
            enabled = !isProcessing && lines.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("assemble_story_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonPurple,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.AutoStories, contentDescription = "Assemble")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isProcessing) "Assembling Production..." else "Assemble Master Audio Drama",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
