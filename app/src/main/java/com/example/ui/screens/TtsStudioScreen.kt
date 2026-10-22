package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.voices.VoiceCatalog
import com.example.ui.MainViewModel
import com.example.ui.TextToSpeechViewModel
import com.example.ui.TtsGenerationStatus
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TtsStudioScreen(
    mainViewModel: MainViewModel? = null,
    ttsViewModel: TextToSpeechViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val text by ttsViewModel.scriptText.collectAsState()
    val title by ttsViewModel.projectTitle.collectAsState()
    val wordCount by ttsViewModel.wordCount.collectAsState()
    val charCount by ttsViewModel.charCount.collectAsState()

    val selectedVoice by ttsViewModel.selectedVoice.collectAsState()
    val pitchOffset by ttsViewModel.pitchOffset.collectAsState()
    val speedOffset by ttsViewModel.speedOffset.collectAsState()
    val stability by ttsViewModel.stability.collectAsState()
    val expressiveness by ttsViewModel.expressiveness.collectAsState()
    val generationStatus by ttsViewModel.generationStatus.collectAsState()
    val lastGeneratedFile by ttsViewModel.lastGeneratedFile.collectAsState()

    val isPlaying by ttsViewModel.audioEngine.isPlaying.collectAsState()
    val playbackProgress by ttsViewModel.audioEngine.playbackProgress.collectAsState()

    var showVoiceDialog by remember { mutableStateOf(false) }

    val directionTags = listOf(
        "[whispering]", "[excited]", "[sad]", "[angry]", "[calm]", "[dramatic pause]"
    )

    val sampleCategories = listOf("Documentary", "Audiobook", "Commercial", "Podcast")

    val isGenerating = generationStatus is TtsGenerationStatus.Generating || generationStatus is TtsGenerationStatus.Preparing

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("tts_studio_screen")
    ) {
        // Studio Workspace Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Text to Speech Studio",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "High-fidelity neural synthesis with emotion direction",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = StudioCard,
                border = BorderStroke(1.dp, StudioBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(StudioEmerald)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Engine Ready",
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioEmerald,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Sample Scripts Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Load Sample:",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(sampleCategories) { cat ->
                    FilterChip(
                        selected = false,
                        onClick = { ttsViewModel.loadSampleScript(cat) },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = StudioCard,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = false,
                            borderColor = StudioBorder
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Project Title Input
        OutlinedTextField(
            value = title,
            onValueChange = { ttsViewModel.updateProjectTitle(it) },
            label = { Text("Project Title") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tts_title_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = StudioBorder,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Script Direction Tags Row
        Text(
            text = "Insert Script Directions & Emotions:",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(directionTags) { tag ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = StudioCard,
                    border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable {
                        ttsViewModel.insertDirectionTag(tag)
                    }
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPurple,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Large Script Text Editor
        OutlinedTextField(
            value = text,
            onValueChange = { ttsViewModel.updateScriptText(it) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp, max = 260.dp)
                .testTag("tts_text_input"),
            placeholder = { Text("Paste or type your script here (supports up to 30,000 words with auto-chunking)...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = StudioBorder,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(14.dp)
        )

        // Word & Character Counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$wordCount words • $charCount characters",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Text(
                text = if (wordCount > 100) "Long-form Auto-Chunking Active" else "Standard Synthesis",
                style = MaterialTheme.typography.labelSmall,
                color = if (wordCount > 100) StudioAmber else NeonCyan
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selected Voice Card Selector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showVoiceDialog = true }
                .testTag("select_voice_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "Voice",
                            tint = StudioDarkBg,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = selectedVoice.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${selectedVoice.gender} • ${selectedVoice.accent} • ${selectedVoice.styleCategory}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                Button(
                    onClick = { showVoiceDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioBorder),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Change", style = MaterialTheme.typography.labelMedium, color = NeonCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Speed & Pitch & Expressiveness Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, StudioBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Speech Rate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Speech Rate (Pacing)", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    Text(
                        text = String.format("%.2fx", selectedVoice.speedMultiplier + speedOffset),
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = speedOffset,
                    onValueChange = { ttsViewModel.updateSpeedOffset(it) },
                    valueRange = -0.5f..0.5f,
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Pitch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Voice Pitch", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    Text(
                        text = String.format("%.2fx", selectedVoice.pitchMultiplier + pitchOffset),
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonPurple,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = pitchOffset,
                    onValueChange = { ttsViewModel.updatePitchOffset(it) },
                    valueRange = -0.4f..0.4f,
                    colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Stability & Expressiveness
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Style Expressiveness", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    Text(
                        text = "${(expressiveness * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = StudioAmber,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = expressiveness,
                    onValueChange = { ttsViewModel.updateExpressiveness(it) },
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = StudioAmber, activeTrackColor = StudioAmber)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Generation Job Status Card
        when (val status = generationStatus) {
            is TtsGenerationStatus.Preparing -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioConsole),
                    border = BorderStroke(1.dp, StudioAmber)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = StudioAmber,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Preparing text segmentation & pronunciation rules...",
                            style = MaterialTheme.typography.bodySmall,
                            color = StudioAmber
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
            is TtsGenerationStatus.Generating -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioConsole),
                    border = BorderStroke(1.dp, StudioAmber)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = status.statusMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = StudioAmber
                            )
                            Text(
                                text = "${(status.progress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = StudioAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { status.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = StudioAmber,
                            trackColor = StudioBorder
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { ttsViewModel.cancelGeneration() }) {
                                Text("Cancel Generation", color = StudioRed, fontSize = 12.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
            is TtsGenerationStatus.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioConsole),
                    border = BorderStroke(1.dp, StudioRed)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = "Error", tint = StudioRed)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = status.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = StudioRed
                            )
                        }
                        IconButton(onClick = { ttsViewModel.dismissError() }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextMuted)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
            is TtsGenerationStatus.Success -> {
                GeneratedVoiceDownloadCard(
                    file = status.audioFile,
                    durationMs = status.durationMs,
                    isPlaying = isPlaying,
                    playbackProgress = playbackProgress,
                    onTogglePlay = { ttsViewModel.audioEngine.togglePlayback(status.audioFile.absolutePath) },
                    onSeek = { ttsViewModel.audioEngine.seekToFraction(it) },
                    onDownload = { ttsViewModel.downloadGeneratedAudio(status.audioFile) },
                    onShare = { ttsViewModel.shareGeneratedAudio(status.audioFile) }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
            is TtsGenerationStatus.Idle -> {
                if (lastGeneratedFile != null && lastGeneratedFile!!.exists()) {
                    GeneratedVoiceDownloadCard(
                        file = lastGeneratedFile!!,
                        durationMs = ttsViewModel.audioEngine.durationMs.collectAsState().value,
                        isPlaying = isPlaying,
                        playbackProgress = playbackProgress,
                        onTogglePlay = { ttsViewModel.audioEngine.togglePlayback(lastGeneratedFile!!.absolutePath) },
                        onSeek = { ttsViewModel.audioEngine.seekToFraction(it) },
                        onDownload = { ttsViewModel.downloadGeneratedAudio(lastGeneratedFile!!) },
                        onShare = { ttsViewModel.shareGeneratedAudio(lastGeneratedFile!!) }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }

        // Primary Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { ttsViewModel.auditionSelectedVoice() },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("audition_sample_btn"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, StudioBorder)
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Preview", tint = TextPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Audition Voice", color = TextPrimary)
            }

            Button(
                onClick = {
                    ttsViewModel.synthesizeSpeech { project ->
                        mainViewModel?.setActiveFile(java.io.File(project.audioFilePath))
                    }
                },
                enabled = !isGenerating && text.isNotBlank(),
                modifier = Modifier
                    .weight(1.3f)
                    .height(52.dp)
                    .testTag("generate_tts_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = StudioDarkBg
                )
            ) {
                Icon(Icons.Default.GraphicEq, contentDescription = "Generate")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isGenerating) "Generating..." else "Synthesize Speech",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Voice Selector Dialog
    if (showVoiceDialog) {
        VoiceSelectorDialog(
            currentSelected = selectedVoice,
            onSelect = {
                ttsViewModel.selectVoice(it)
                showVoiceDialog = false
            },
            onDismiss = { showVoiceDialog = false }
        )
    }
}

@Composable
fun VoiceSelectorDialog(
    currentSelected: com.example.data.model.VoiceProfile,
    onSelect: (com.example.data.model.VoiceProfile) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) VoiceCatalog.allVoices.take(30)
        else VoiceCatalog.allVoices.filter {
            it.name.contains(searchQuery, true) ||
            it.gender.contains(searchQuery, true) ||
            it.accent.contains(searchQuery, true) ||
            it.styleCategory.contains(searchQuery, true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Choose Studio Voice", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        },
        text = {
            Column(modifier = Modifier.heightIn(max = 420.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search 200+ voices...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                androidx.compose.foundation.lazy.LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered) { voice ->
                        val isSel = voice.id == currentSelected.id
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) StudioCardHover else StudioCard,
                            border = if (isSel) BorderStroke(1.dp, NeonCyan) else BorderStroke(1.dp, StudioBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(voice) }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = voice.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${voice.gender} • ${voice.accent} • ${voice.styleCategory}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                                if (isSel) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = NeonCyan)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = NeonCyan)
            }
        },
        containerColor = StudioConsole
    )
}

@Composable
fun GeneratedVoiceDownloadCard(
    file: java.io.File,
    durationMs: Long,
    isPlaying: Boolean,
    playbackProgress: Float,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("generated_voice_download_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DownloadDone,
                            contentDescription = "Ready",
                            tint = StudioDarkBg,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Generated Voice Ready",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${file.name} • ${file.length() / 1024} KB • ${com.example.ui.components.formatDuration(durationMs)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan
                        )
                    }
                }

                IconButton(
                    onClick = onTogglePlay,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("preview_play_btn")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                        contentDescription = "Play/Pause",
                        tint = NeonCyan,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            WaveformVisualizer(
                progress = playbackProgress,
                isPlaying = isPlaying,
                onSeek = onSeek
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDownload,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(46.dp)
                        .testTag("download_voice_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = StudioDarkBg
                    )
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Download")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Download Voice (.wav)", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("share_voice_btn"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, StudioBorder)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = TextPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export / Share", color = TextPrimary)
                }
            }
        }
    }
}

