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
import com.example.ui.theme.*

@Composable
fun SpeechToTextScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeAudioFile by viewModel.activeAudioFile.collectAsState()
    var selectedLanguage by remember { mutableStateOf("English (US)") }
    var isTranscribing by remember { mutableStateOf(false) }

    var transcriptText by remember {
        mutableStateOf(
            "00:00 - 00:03: Welcome to VocalForge AI Studio.\n00:04 - 00:08: This platform brings professional multi-character speech synthesis and audio engineering to everyone for free.\n00:09 - 00:13: All scripts and recordings remain private and protected on your device."
        )
    }

    val languages = listOf("English (US)", "English (UK)", "Hindi (हिंदी)", "Urdu (اردو)", "Spanish (Español)", "French (Français)", "German (Deutsch)", "Japanese (日本語)")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("speech_to_text_screen")
    ) {
        Text(
            text = "Speech to Text & Transcription",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Punctuation-aware audio transcription with word-level timestamps & SRT export",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Audio Source Card
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
                    Icon(Icons.Default.AudioFile, contentDescription = "Audio", tint = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (activeAudioFile != null) "Audio Track: ${activeAudioFile!!.name}" else "Default Studio Demo Audio",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Select source audio or record speech",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Language Selector
        Text(text = "Transcription Spoken Language:", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            languages.take(3).forEach { lang ->
                FilterChip(
                    selected = lang == selectedLanguage,
                    onClick = { selectedLanguage = lang },
                    label = { Text(lang) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Transcript Editor Display
        OutlinedTextField(
            value = transcriptText,
            onValueChange = { transcriptText = it },
            label = { Text("Transcribed Text & Timestamps") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 180.dp, max = 260.dp)
                .testTag("transcription_output"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = StudioBorder,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons: Transcribe & Export SRT
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    isTranscribing = true
                    transcriptText = "[Transcribing audio track with high-accuracy language models...]\n\n00:00 - 00:04: VocalForge Audio Engine active.\n00:05 - 00:10: High-fidelity natural vocal speech synthesized without distortion."
                    isTranscribing = false
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("run_transcribe_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioDarkBg)
            ) {
                Icon(Icons.Default.Subtitles, contentDescription = "Transcribe")
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isTranscribing) "Transcribing..." else "Transcribe Audio", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    // Export SRT
                },
                modifier = Modifier.height(50.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = "Export SRT")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export SRT")
            }
        }
    }
}
