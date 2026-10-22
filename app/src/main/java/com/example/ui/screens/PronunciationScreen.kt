package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun PronunciationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val rules by viewModel.allRules.collectAsState()
    val selectedVoice by viewModel.selectedVoice.collectAsState()

    var word by remember { mutableStateOf("") }
    var replacement by remember { mutableStateOf("") }
    var isCaseSensitive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("pronunciation_screen")
    ) {
        Text(
            text = "Pronunciation Dictionary",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Define phonetic respelling & acronym rules injected into all speech models",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Add Rule Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, StudioBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Add Phonetic Respelling Rule", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = word,
                        onValueChange = { word = it },
                        label = { Text("Original Word") },
                        placeholder = { Text("e.g. AI, SQL, Nguyen") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pronunciation_word_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = replacement,
                        onValueChange = { replacement = it },
                        label = { Text("Phonetic Respelling") },
                        placeholder = { Text("e.g. A.I., Sequel, Win") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pronunciation_replacement_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isCaseSensitive,
                            onCheckedChange = { isCaseSensitive = it },
                            colors = CheckboxDefaults.colors(checkedColor = NeonCyan)
                        )
                        Text(text = "Case Sensitive", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                    }

                    Button(
                        onClick = {
                            if (word.isNotBlank() && replacement.isNotBlank()) {
                                viewModel.addPronunciationRule(word, replacement, isCaseSensitive)
                                word = ""
                                replacement = ""
                            }
                        },
                        enabled = word.isNotBlank() && replacement.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioDarkBg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_rule_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Rule", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Existing Rules List
        Text(text = "Active Pronunciation Rules (${rules.size}):", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (rules.isEmpty()) {
                item {
                    Text(
                        text = "No custom rules defined yet. Standard phonetic dictionaries active.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(rules, key = { it.id }) { rule ->
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = rule.wordToReplace, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = "to", tint = NeonCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = rule.phoneticReplacement, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = NeonPurple)
                                }
                                Text(
                                    text = if (rule.isCaseSensitive) "Exact Case" else "Case Insensitive",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        viewModel.generateTts(rule.phoneticReplacement, selectedVoice, title = "Pronunciation Test")
                                    }
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Audition", tint = NeonCyan)
                                }
                                IconButton(
                                    onClick = { viewModel.deletePronunciationRule(rule) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
