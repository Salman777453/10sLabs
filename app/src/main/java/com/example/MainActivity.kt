package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.StudioWorkspace
import com.example.ui.MainViewModel
import com.example.ui.components.StudioPlayerBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                VocalForgeMainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocalForgeMainApp(viewModel: MainViewModel) {
    val currentWorkspace by viewModel.currentWorkspace.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    // Player state
    val isPlaying by viewModel.audioEngine.isPlaying.collectAsState()
    val currentPlayingPath by viewModel.audioEngine.currentPlayingPath.collectAsState()
    val progress by viewModel.audioEngine.playbackProgress.collectAsState()
    val currentMs by viewModel.audioEngine.currentPositionMs.collectAsState()
    val durationMs by viewModel.audioEngine.durationMs.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Intercept hardware Android back button to navigate previous workspace or close drawer
    BackHandler {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            val handled = viewModel.handleBackPress()
            if (!handled) {
                // At root, let system handle exit
            }
        }
    }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearStatusMessage()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerContentColor = TextPrimary,
                modifier = Modifier.width(310.dp)
            ) {
                // Drawer Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.15f), NeonPurple.copy(alpha = 0.15f))))
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.GraphicEq, contentDescription = "Logo", tint = StudioDarkBg, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "VocalForge",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Professional AI Voice Studio",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan
                    )
                }

                HorizontalDivider(color = StudioBorder)

                // Workspaces List grouped by category
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    val grouped = StudioWorkspace.values().groupBy { it.category }

                    grouped.forEach { (category, workspaces) ->
                        Text(
                            text = category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                        )

                        workspaces.forEach { workspace ->
                            val isSelected = workspace == currentWorkspace
                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        imageVector = getIconForWorkspace(workspace),
                                        contentDescription = workspace.title,
                                        tint = if (isSelected) NeonCyan else TextSecondary
                                    )
                                },
                                label = {
                                    Text(
                                        text = workspace.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) NeonCyan else TextPrimary
                                    )
                                },
                                selected = isSelected,
                                onClick = {
                                    viewModel.navigateTo(workspace)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 2.dp)
                                    .testTag("nav_item_${workspace.name.lowercase()}"),
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = StudioCardHover,
                                    unselectedContainerColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.GraphicEq, contentDescription = "Logo", tint = StudioDarkBg, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentWorkspace.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("drawer_menu_button")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Open Studio Menu", tint = NeonCyan)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.toggleTheme() }) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                                tint = NeonCyan
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                Column {
                    // Floating Player Bar if an audio track is currently loaded or playing
                    if (currentPlayingPath != null) {
                        StudioPlayerBar(
                            title = java.io.File(currentPlayingPath!!).name,
                            isPlaying = isPlaying,
                            progress = progress,
                            currentMs = currentMs,
                            durationMs = durationMs,
                            onTogglePlay = { currentPlayingPath?.let { viewModel.audioEngine.togglePlayback(it) } },
                            onSeek = { viewModel.audioEngine.seekToFraction(it) },
                            onClose = { viewModel.audioEngine.stopAudio() },
                            onDownload = {
                                currentPlayingPath?.let { path ->
                                    val file = java.io.File(path)
                                    viewModel.downloadAudio(file)
                                }
                            }
                        )
                    }

                    // Bottom Navigation Bar for rapid one-tap creation switching
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = TextPrimary,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        val quickItems = listOf(
                            StudioWorkspace.TTS_STUDIO,
                            StudioWorkspace.VOICE_LIBRARY,
                            StudioWorkspace.STORY_STUDIO,
                            StudioWorkspace.AUDIO_EDITOR,
                            StudioWorkspace.PROJECTS_HISTORY
                        )

                        quickItems.forEach { ws ->
                            val isSelected = ws == currentWorkspace
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = getIconForWorkspace(ws),
                                        contentDescription = ws.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = when (ws) {
                                            StudioWorkspace.TTS_STUDIO -> "TTS"
                                            StudioWorkspace.VOICE_LIBRARY -> "Voices"
                                            StudioWorkspace.STORY_STUDIO -> "Story"
                                            StudioWorkspace.AUDIO_EDITOR -> "Editor"
                                            StudioWorkspace.PROJECTS_HISTORY -> "Projects"
                                            else -> ws.title
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(ws) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = StudioDarkBg,
                                    selectedTextColor = NeonCyan,
                                    indicatorColor = NeonCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("bottom_tab_${ws.name.lowercase()}")
                            )
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Workspace Content Switcher
                AnimatedContent(
                    targetState = currentWorkspace,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "workspace_transition"
                ) { ws ->
                    when (ws) {
                        StudioWorkspace.TTS_STUDIO -> TtsStudioScreen(viewModel)
                        StudioWorkspace.VOICE_LIBRARY -> VoiceLibraryScreen(viewModel)
                        StudioWorkspace.STORY_STUDIO -> MultiCharacterStoryScreen(viewModel)
                        StudioWorkspace.VOICE_CLONING -> VoiceCloningScreen(viewModel)
                        StudioWorkspace.VOICE_CHANGER -> VoiceChangerScreen(viewModel)
                        StudioWorkspace.VOICE_RECORDER -> VoiceRecorderScreen(viewModel)
                        StudioWorkspace.NOISE_REMOVAL -> NoiseRemovalScreen(viewModel)
                        StudioWorkspace.VOICE_ENHANCE -> VoiceEnhancementScreen(viewModel)
                        StudioWorkspace.AUDIO_EDITOR -> AudioEditorScreen(viewModel)
                        StudioWorkspace.SPEECH_TO_TEXT -> SpeechToTextScreen(viewModel)
                        StudioWorkspace.DUBBING -> DubbingScreen(viewModel)
                        StudioWorkspace.SOUND_EFFECTS -> SoundEffectsScreen(viewModel)
                        StudioWorkspace.PRONUNCIATION -> PronunciationScreen(viewModel)
                        StudioWorkspace.PROJECTS_HISTORY -> ProjectsHistoryScreen(viewModel)
                        StudioWorkspace.EXPORT_CENTER -> ExportCenterScreen(viewModel)
                        StudioWorkspace.SETTINGS -> SettingsDashboardScreen(viewModel)
                    }
                }
            }
        }
    }
}

fun getIconForWorkspace(workspace: StudioWorkspace): ImageVector {
    return when (workspace) {
        StudioWorkspace.TTS_STUDIO -> Icons.Default.Mic
        StudioWorkspace.VOICE_LIBRARY -> Icons.Default.RecordVoiceOver
        StudioWorkspace.STORY_STUDIO -> Icons.Default.AutoStories
        StudioWorkspace.VOICE_CLONING -> Icons.Default.Fingerprint
        StudioWorkspace.VOICE_CHANGER -> Icons.Default.GraphicEq
        StudioWorkspace.VOICE_RECORDER -> Icons.Default.RadioButtonChecked
        StudioWorkspace.NOISE_REMOVAL -> Icons.Default.HearingDisabled
        StudioWorkspace.VOICE_ENHANCE -> Icons.Default.Tune
        StudioWorkspace.AUDIO_EDITOR -> Icons.Default.VolumeUp
        StudioWorkspace.SPEECH_TO_TEXT -> Icons.Default.Subtitles
        StudioWorkspace.DUBBING -> Icons.Default.Translate
        StudioWorkspace.SOUND_EFFECTS -> Icons.Default.SurroundSound
        StudioWorkspace.PRONUNCIATION -> Icons.Default.Spellcheck
        StudioWorkspace.PROJECTS_HISTORY -> Icons.Default.Folder
        StudioWorkspace.EXPORT_CENTER -> Icons.Default.Download
        StudioWorkspace.SETTINGS -> Icons.Default.Settings
    }
}
