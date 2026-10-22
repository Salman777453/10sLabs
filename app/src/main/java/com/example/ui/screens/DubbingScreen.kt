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
import com.example.data.voices.VoiceCatalog
import com.example.ui.MainViewModel
import com.example.ui.components.WaveformVisualizer
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

@Composable
fun DubbingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var sourceText by remember {
        mutableStateOf(
            "Hello, and welcome to the international documentary series exploring the mysteries of the natural world."
        )
    }
    var targetLanguage by remember { mutableStateOf("Spanish") }

    val selectedVoice by viewModel.selectedVoice.collectAsState()
    val isProcessing by viewModel.isProcessingJob.collectAsState()
    val activeAudioFile by viewModel.activeAudioFile.collectAsState()
    val playbackProgress by viewModel.audioEngine.playbackProgress.collectAsState()
    val isPlaying by viewModel.audioEngine.isPlaying.collectAsState()

    val targetLanguages = listOf("Spanish", "French", "German", "Hindi", "Urdu", "Japanese", "Italian")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("dubbing_screen")
    ) {
        Text(
            text = "Dubbing & Translation Studio",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Multilingual speech translation, native dubbing & audio localization",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Source Script Text Area
        OutlinedTextField(
            value = sourceText,
            onValueChange = { sourceText = it },
            label = { Text("Original Script / Audio Transcript") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = StudioBorder,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Target Language Selection Chips
        Text(text = "Target Localization Language:", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            targetLanguages.take(4).forEach { lang ->
                FilterChip(
                    selected = lang == targetLanguage,
                    onClick = { targetLanguage = lang },
                    label = { Text(lang) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Voice Selector for Dubbing
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
                Column {
                    Text(text = "Dubbing Voice: ${selectedVoice.name}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "${selectedVoice.gender} • ${selectedVoice.accent} • ${selectedVoice.styleCategory}", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                }
                IconButton(onClick = { viewModel.auditionVoice(selectedVoice) }) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Preview", tint = NeonCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Render Waveform Output
        if (activeAudioFile != null && activeAudioFile!!.exists()) {
            Text(text = "Generated Dubbed Audio:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            WaveformVisualizer(
                progress = playbackProgress,
                isPlaying = isPlaying,
                activeColor = NeonPurple,
                onSeek = { viewModel.audioEngine.seekToFraction(it) }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Action Button
        Button(
            onClick = {
                viewModel.dubAndTranslate(sourceText, targetLanguage, selectedVoice)
            },
            enabled = !isProcessing && sourceText.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("run_dubbing_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = Color.White)
        ) {
            Icon(Icons.Default.Translate, contentDescription = "Dub")
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isProcessing) "Translating & Dubbing..." else "Generate Dubbed Speech", fontWeight = FontWeight.Bold)
        }
    }
}
