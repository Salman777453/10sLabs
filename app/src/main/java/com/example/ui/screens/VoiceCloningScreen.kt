package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.ui.MainViewModel
import com.example.ui.components.MetricGauge
import com.example.ui.theme.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCloningScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var voiceName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Female") }
    var consentAgreed by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    val clonedVoices by viewModel.clonedVoices.collectAsState()
    val isRecording by viewModel.audioEngine.isRecordingActive.collectAsState()
    val decibels by viewModel.audioEngine.recordingDecibels.collectAsState()
    val recordedSeconds by viewModel.audioEngine.recordedSeconds.collectAsState()

    var sampleFile by remember { mutableStateOf<File?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("voice_cloning_screen")
    ) {
        Text(
            text = "Voice Cloning Studio",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Consent-verified acoustic cloning & timbre calibration",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Recording & Quality Inspection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, if (isRecording) StudioAmber else StudioBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isRecording) "Recording Voice Sample..." else if (sampleFile != null) "Sample Ready for Cloning" else "Record Voice Sample",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isRecording) StudioAmber else TextPrimary
                        )
                        Text(
                            text = "Speak clearly for 5–10 seconds in a quiet room",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = StudioConsole
                    ) {
                        Text(
                            text = "${recordedSeconds}s",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isRecording) StudioAmber else NeonCyan,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // VU Meter
                MetricGauge(
                    label = "Microphone Signal Level (RMS)",
                    value = ((decibels + 60f) / 60f).coerceIn(0f, 1f),
                    displayString = String.format("%.1f dB", decibels),
                    color = if (decibels > -5f) StudioRed else if (isRecording) StudioAmber else NeonCyan
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (isRecording) {
                                viewModel.audioEngine.stopMicrophoneRecording()
                            } else {
                                val file = File(viewModel.getApplication<android.app.Application>().cacheDir, "clone_sample_${System.currentTimeMillis()}.wav")
                                sampleFile = file
                                viewModel.audioEngine.startMicrophoneRecording(file)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("record_sample_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) StudioRed else NeonCyan,
                            contentColor = if (isRecording) Color.White else StudioDarkBg
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(if (isRecording) Icons.Default.Stop else Icons.Default.Mic, contentDescription = "Record")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRecording) "Stop Recording" else "Record 10s Sample", fontWeight = FontWeight.Bold)
                    }

                    if (sampleFile != null && !isRecording) {
                        OutlinedButton(
                            onClick = {
                                viewModel.audioEngine.playAudio(sampleFile!!.absolutePath)
                            },
                            modifier = Modifier.height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Play")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Profile Configuration
        OutlinedTextField(
            value = voiceName,
            onValueChange = { voiceName = it },
            label = { Text("Cloned Voice Name") },
            placeholder = { Text("e.g. My Studio Voice, Custom Narrator") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("clone_name_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = StudioBorder,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                selected = gender == "Female",
                onClick = { gender = "Female" },
                label = { Text("Female Timbre") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = gender == "Male",
                onClick = { gender = "Male" },
                label = { Text("Male Timbre") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Mandatory Consent & Safety Checkbox
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = StudioConsole),
            border = BorderStroke(1.dp, if (consentAgreed) NeonPurple else StudioBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = consentAgreed,
                    onCheckedChange = { consentAgreed = it },
                    colors = CheckboxDefaults.colors(checkedColor = NeonPurple),
                    modifier = Modifier.testTag("consent_checkbox")
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "I verify that I own or have explicit authorization to clone this voice. I agree not to impersonate public figures or generate deceptive audio.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                sampleFile?.let { file ->
                    viewModel.saveClonedVoice(
                        name = voiceName,
                        gender = gender,
                        samplePath = file.absolutePath,
                        consentConfirmed = consentAgreed,
                        description = description
                    )
                    voiceName = ""
                }
            },
            enabled = consentAgreed && voiceName.isNotBlank() && sampleFile != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("save_clone_btn"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = Color.White)
        ) {
            Icon(Icons.Default.Fingerprint, contentDescription = "Clone")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Calibrate & Save Cloned Profile", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Saved Cloned Profiles List
        Text(
            text = "Saved Authorized Voice Profiles (${clonedVoices.size}):",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (clonedVoices.isEmpty()) {
                item {
                    Text(
                        text = "No custom cloned voices yet. Record your first sample above.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(clonedVoices, key = { it.id }) { clone ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
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
                                Text(text = clone.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = "${clone.gender} • F0 ~${clone.estimatedPitchF0.toInt()}Hz • Quality ${clone.qualityScore}%", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                            }
                            IconButton(onClick = { viewModel.audioEngine.playAudio(clone.baseSamplePath) }) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Play", tint = NeonCyan)
                            }
                        }
                    }
                }
            }
        }
    }
}
