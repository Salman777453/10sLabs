package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudioWorkspace
import com.example.ui.MainViewModel
import com.example.ui.components.MetricGauge
import com.example.ui.theme.*
import java.io.File

@Composable
fun VoiceRecorderScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isRecording by viewModel.audioEngine.isRecordingActive.collectAsState()
    val decibels by viewModel.audioEngine.recordingDecibels.collectAsState()
    val recordedSeconds by viewModel.audioEngine.recordedSeconds.collectAsState()
    val activeAudioFile by viewModel.activeAudioFile.collectAsState()

    var lastRecordedFile by remember { mutableStateOf<File?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("voice_recorder_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Voice Recorder Studio",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Studio-grade microphone capture with live metering",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Large Recording Timer Display
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = StudioConsole,
            border = BorderStroke(1.dp, if (isRecording) StudioRed else StudioBorder),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = String.format("%02d:%02d", recordedSeconds / 60, recordedSeconds % 60),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isRecording) StudioRed else TextPrimary
                )
                Text(
                    text = if (isRecording) "RECORDING IN PROGRESS" else "STANDBY",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isRecording) StudioAmber else TextMuted,
                    letterSpacing = 1.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // VU Meter
        MetricGauge(
            label = "Input Decibel Level (VU)",
            value = ((decibels + 60f) / 60f).coerceIn(0f, 1f),
            displayString = String.format("%.1f dB", decibels),
            color = if (decibels > -3f) StudioRed else if (isRecording) StudioAmber else NeonCyan,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Big Record Button
        IconButton(
            onClick = {
                if (isRecording) {
                    viewModel.audioEngine.stopMicrophoneRecording()
                    lastRecordedFile?.let { viewModel.setActiveFile(it) }
                } else {
                    val file = File(viewModel.getApplication<android.app.Application>().filesDir, "recording_${System.currentTimeMillis()}.wav")
                    lastRecordedFile = file
                    viewModel.audioEngine.startMicrophoneRecording(file)
                }
            },
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(if (isRecording) StudioRed else NeonCyan)
                .testTag("big_record_button")
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = "Record Toggle",
                tint = StudioDarkBg,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Routing Options
        if (lastRecordedFile != null && lastRecordedFile!!.exists() && !isRecording) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Recording Captured: ${lastRecordedFile!!.name}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.audioEngine.playAudio(lastRecordedFile!!.absolutePath) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Play")
                        }
                        Button(
                            onClick = {
                                viewModel.setActiveFile(lastRecordedFile!!)
                                viewModel.navigateTo(StudioWorkspace.NOISE_REMOVAL)
                            },
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text("Clean Noise", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
