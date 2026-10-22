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
import com.example.BuildConfig
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.io.File

@Composable
fun SettingsDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    var selectedEngine by remember { mutableStateOf("Android Neural DSP Engine (Free & On-Device)") }

    val hasGeminiKey = remember { BuildConfig.GEMINI_API_KEY.isNotBlank() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("settings_dashboard_screen")
    ) {
        Text(
            text = "Settings & Privacy Dashboard",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "System diagnostics, voice privacy, data protection & free access parameters",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Appearance Theme Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, StudioBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Studio Dark Interface", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = if (isDarkTheme) "High-contrast cyberpunk studio dark" else "Accessible studio light mode", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { viewModel.toggleTheme() },
                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // AI Engine & Processing Architecture
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, StudioBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "AI Voice Engine & Processing Architecture", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = StudioEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "On-Device Neural Speech Engine: Operational (100% Free)", style = MaterialTheme.typography.labelSmall, color = StudioEmerald)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (hasGeminiKey) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = "Cloud AI",
                        tint = if (hasGeminiKey) StudioEmerald else NeonPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (hasGeminiKey) "Gemini Cloud Script Engine: Connected" else "Cloud AI: Optional (Local Offline Heuristics Active)",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (hasGeminiKey) StudioEmerald else TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Core synthesis runs 100% on-device utilizing optimized local neural speech synthesis and PCM time-domain DSP. No external API fees or credit cards required.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Privacy & Consent Disclosure (Crucial requirement #20)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = StudioConsole),
            border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = "Security", tint = NeonPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Data Isolation & Voice Ethics Policy", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Private by Default: User recordings, scripts, and synthesized audio files are stored strictly on-device in private app sandboxed storage.\n• Zero Training: Uploaded or recorded audio is never utilized to train third-party public models without explicit user consent.\n• Anti-Impersonation: Voice cloning requires mandatory identity authorization verification. Impersonation of non-consenting individuals or public figures is strictly prohibited.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Storage & Cache Cleanup
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, StudioBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Cache Management", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Clear temporary audio chunks and cached audition samples.", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        try {
                            viewModel.getApplication<android.app.Application>().cacheDir.deleteRecursively()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, StudioBorder)
                ) {
                    Icon(Icons.Default.CleaningServices, contentDescription = "Clear Cache", tint = TextPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clear Temporary Cache", color = TextPrimary)
                }
            }
        }
    }
}
