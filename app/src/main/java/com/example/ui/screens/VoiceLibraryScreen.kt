package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.StudioWorkspace
import com.example.data.model.VoiceProfile
import com.example.data.voices.VoiceCatalog
import com.example.ui.MainViewModel
import com.example.ui.components.VoiceCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceLibraryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val selectedVoice by viewModel.selectedVoice.collectAsState()
    val playingPath by viewModel.audioEngine.currentPlayingPath.collectAsState()
    val isPlaying by viewModel.audioEngine.isPlaying.collectAsState()

    val categories = listOf(
        "All", "Male", "Female", "Youth", "Deep Bass", "Narration", "Podcast", "Cartoon", "Commercial", "News", "Hindi", "Urdu", "Spanish"
    )

    val filteredVoices = remember(searchQuery, selectedCategory) {
        VoiceCatalog.allVoices.filter { voice ->
            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "Male" -> voice.gender.equals("Male", true)
                "Female" -> voice.gender.equals("Female", true)
                "Youth" -> voice.gender.equals("Youth", true) || voice.ageGroup.equals("Youth", true)
                "Deep Bass" -> voice.styleCategory.contains("Bass", true)
                "Narration" -> voice.styleCategory.contains("Narration", true)
                "Podcast" -> voice.styleCategory.contains("Podcast", true)
                "Cartoon" -> voice.styleCategory.contains("Cartoon", true)
                "Commercial" -> voice.styleCategory.contains("Commercial", true)
                "News" -> voice.styleCategory.contains("News", true)
                "Hindi" -> voice.languageCode.startsWith("hi")
                "Urdu" -> voice.languageCode.startsWith("ur")
                "Spanish" -> voice.languageCode.startsWith("es")
                else -> true
            }

            val matchesSearch = if (searchQuery.isBlank()) true else {
                voice.name.contains(searchQuery, true) ||
                voice.accent.contains(searchQuery, true) ||
                voice.toneDescription.contains(searchQuery, true) ||
                voice.styleCategory.contains(searchQuery, true)
            }

            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("voice_library_screen")
    ) {
        // Library Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "AI Voice Library",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Catalog of ${VoiceCatalog.allVoices.size}+ distinct voice profiles",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = StudioCard,
                border = BorderStroke(1.dp, StudioBorder)
            ) {
                Text(
                    text = "${filteredVoices.size} Voices",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name, accent, tone, or style...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = NeonCyan) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("library_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = StudioBorder,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan,
                        selectedLabelColor = StudioDarkBg,
                        containerColor = StudioCard,
                        labelColor = TextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) NeonCyan else StudioBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Voice List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredVoices, key = { it.id }) { voice ->
                val isSel = voice.id == selectedVoice.id
                val isThisPlaying = isPlaying && playingPath?.contains(voice.id) == true

                Column {
                    VoiceCard(
                        voice = voice,
                        isSelected = isSel,
                        isPlaying = isThisPlaying,
                        onSelect = { viewModel.selectVoice(voice) },
                        onAudition = { viewModel.auditionVoice(voice) }
                    )

                    if (isSel) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, start = 8.dp, end = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    viewModel.selectVoice(voice)
                                    viewModel.navigateTo(StudioWorkspace.TTS_STUDIO)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "Use", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Use in Studio", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
