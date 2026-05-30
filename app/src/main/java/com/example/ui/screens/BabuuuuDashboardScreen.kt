package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Chronic
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.BabuuuuTab
import com.example.ui.viewmodel.BabuuuuViewModel
import com.example.ui.viewmodel.ChatContact
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BabuuuuDashboardScreen(
    viewModel: BabuuuuViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val chronicles by viewModel.allChronicles.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCommentChronicId by viewModel.selectedChronicIdForComments.collectAsStateWithLifecycle()
    
    // Snackbar helper state for space transmissions
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Follower state for Trending Nexus
    val synchronizedNodes = remember { mutableStateMapOf<String, Boolean>() }

    CyberGridBackground(modifier = modifier) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isCompact = maxWidth < 700.dp

            if (isCompact) {
                // Mobile Portrait layout
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Logo
                                Text(
                                    text = "Babuuuu",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    style = TextStyle(
                                        brush = Brush.horizontalGradient(listOf(NeonCyan, SparkViolet))
                                    ),
                                    modifier = Modifier.testTag("app_logo_title")
                                )

                                // Connection Alert Node indicator
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(TerminalGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "LIVE_NODE_OK",
                                        color = TerminalGreen,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            HorizontalDivider(color = Color(0x1F00F2FE), thickness = 1.dp)
                        }
                    },
                    bottomBar = {
                        Column {
                            HorizontalDivider(color = Color(0x1F00F2FE), thickness = 1.dp)
                            // Compact Holographic navigation bar
                            Row(
                                modifier = Modifier
                                    .navigationBarsPadding()
                                    .fillMaxWidth()
                                    .background(Color(0xE0060913))
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NavigationIconCompact(
                                    icon = Icons.Default.Home,
                                    selected = currentTab == BabuuuuTab.HOME,
                                    label = "Feed",
                                    tag = "nav_home_btn",
                                    onClick = { viewModel.setTab(BabuuuuTab.HOME) }
                                )
                                NavigationIconCompact(
                                    icon = Icons.Default.Search,
                                    selected = currentTab == BabuuuuTab.SEARCH,
                                    label = "Scan",
                                    tag = "nav_search_btn",
                                    onClick = { viewModel.setTab(BabuuuuTab.SEARCH) }
                                )
                                NavigationIconCompact(
                                    icon = Icons.Default.Add,
                                    selected = currentTab == BabuuuuTab.CREATE,
                                    label = "Transmit",
                                    tag = "nav_create_btn",
                                    onClick = { viewModel.setTab(BabuuuuTab.CREATE) }
                                )
                                NavigationIconCompact(
                                    icon = Icons.Default.Email,
                                    selected = currentTab == BabuuuuTab.MESSAGES,
                                    label = "Relay",
                                    tag = "nav_messages_btn",
                                    onClick = { viewModel.setTab(BabuuuuTab.MESSAGES) }
                                )
                                NavigationIconCompact(
                                    icon = Icons.Default.Notifications,
                                    selected = currentTab == BabuuuuTab.NOTIFICATIONS,
                                    label = "Alerts",
                                    tag = "nav_alerts_btn",
                                    onClick = { viewModel.setTab(BabuuuuTab.NOTIFICATIONS) }
                                )
                                NavigationIconCompact(
                                    icon = Icons.Default.AccountCircle,
                                    selected = currentTab == BabuuuuTab.PROFILE,
                                    label = "Profile",
                                    tag = "nav_profile_btn",
                                    onClick = { viewModel.setTab(BabuuuuTab.PROFILE) }
                                )
                            }
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) with fadeOut(animationSpec = tween(220))
                            },
                            label = "mobile_tab_transition"
                        ) { targetTab ->
                            when (targetTab) {
                                BabuuuuTab.HOME -> HomeFeedTab(
                                    viewModel = viewModel,
                                    chronicles = chronicles.filter {
                                        it.text.contains(searchQuery, ignoreCase = true) ||
                                                it.username.contains(searchQuery, ignoreCase = true)
                                    },
                                    synchronizedNodes = synchronizedNodes,
                                    onNodeClick = { handle, isSync ->
                                        synchronizedNodes[handle] = isSync
                                    },
                                    onTransmitShare = { text ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Broadcasting transmission: '$text' successfully routed.")
                                        }
                                    }
                                )
                                BabuuuuTab.SEARCH -> SearchTab(
                                    viewModel = viewModel,
                                    searchQuery = searchQuery,
                                    chronicles = chronicles
                                )
                                BabuuuuTab.CREATE -> CreateTab(viewModel = viewModel)
                                BabuuuuTab.MESSAGES -> MessagesTab(viewModel = viewModel)
                                BabuuuuTab.NOTIFICATIONS -> NotificationsTab(viewModel = viewModel)
                                BabuuuuTab.PROFILE -> ProfileTab(viewModel = viewModel, allChronicles = chronicles)
                            }
                        }
                    }
                }
            } else {
                // Wide Screen Tablet/Desktop Layout with Multi-Column Sidebars!
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Column Sidebar
                    LeftSidebarExpanded(
                        currentTab = currentTab,
                        setTab = { viewModel.setTab(it) }
                    )

                    VerticalDivider(color = Color(0x1F00F2FE), thickness = 1.dp)

                    // Middle Feed Column (Chronicles Area)
                    Box(
                        modifier = Modifier
                            .weight(1.8f)
                            .fillMaxHeight()
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) with fadeOut(animationSpec = tween(220))
                            },
                            label = "tablet_tab_transition"
                        ) { targetTab ->
                            when (targetTab) {
                                BabuuuuTab.HOME -> HomeFeedTab(
                                    viewModel = viewModel,
                                    chronicles = chronicles.filter {
                                        it.text.contains(searchQuery, ignoreCase = true) ||
                                                it.username.contains(searchQuery, ignoreCase = true)
                                    },
                                    synchronizedNodes = synchronizedNodes,
                                    onNodeClick = { handle, isSync ->
                                        synchronizedNodes[handle] = isSync
                                    },
                                    onTransmitShare = { text ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Broadcasting transmission: '$text' successfully routed.")
                                        }
                                    }
                                )
                                BabuuuuTab.SEARCH -> SearchTab(
                                    viewModel = viewModel,
                                    searchQuery = searchQuery,
                                    chronicles = chronicles
                                )
                                BabuuuuTab.CREATE -> CreateTab(viewModel = viewModel)
                                BabuuuuTab.MESSAGES -> MessagesTab(viewModel = viewModel)
                                BabuuuuTab.NOTIFICATIONS -> NotificationsTab(viewModel = viewModel)
                                BabuuuuTab.PROFILE -> ProfileTab(viewModel = viewModel, allChronicles = chronicles)
                            }
                        }
                    }

                    VerticalDivider(color = Color(0x1F00F2FE), thickness = 1.dp)

                    // Right Column Sidebar: Trending Nexus & Global Streams
                    RightSidebarExpanded(
                        synchronizedNodes = synchronizedNodes,
                        onNodeClick = { handle, isSync ->
                            synchronizedNodes[handle] = isSync
                        },
                        onHashtagClick = { hashtag ->
                            viewModel.setSearchQuery(hashtag)
                            viewModel.setTab(BabuuuuTab.SEARCH)
                        }
                    )
                }

                // Add Snackbar Host for tablet layout in a separate container overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 24.dp)
                        .navigationBarsPadding(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    SnackbarHost(snackbarHostState)
                }
            }
        }

        // Real-Time Echo comments sheet / dialogue drawer
        if (selectedCommentChronicId != null) {
            EchoCommentSectionDialog(
                chronicId = selectedCommentChronicId!!,
                viewModel = viewModel,
                onDismiss = { viewModel.selectChronicForComments(null) }
            )
        }
    }
}

// Compact Bottom Nav item
@Composable
fun NavigationIconCompact(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    label: String,
    tag: String,
    onClick: () -> Unit
) {
    val color = if (selected) NeonCyan else Color.White.copy(alpha = 0.55f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .testTag(tag)
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label.uppercase(),
            fontSize = 8.sp,
            color = color,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

// Tablet Expanded Left Nav
@Composable
fun LeftSidebarExpanded(
    currentTab: BabuuuuTab,
    setTab: (BabuuuuTab) -> Unit
) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight()
            .background(Color(0xD2060913))
            .padding(18.dp)
            .safeDrawingPadding()
    ) {
        // Futuristic Glowing Header
        Text(
            text = "Babuuuu",
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            style = TextStyle(
                brush = Brush.horizontalGradient(listOf(NeonCyan, SparkViolet))
            ),
            modifier = Modifier
                .padding(bottom = 24.dp)
                .testTag("tablet_logo")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation elements
        NavigationIconExpanded(
            icon = Icons.Default.Home,
            selected = currentTab == BabuuuuTab.HOME,
            label = "Global Feed",
            tag = "tablet_nav_home",
            onClick = { setTab(BabuuuuTab.HOME) }
        )
        NavigationIconExpanded(
            icon = Icons.Default.Search,
            selected = currentTab == BabuuuuTab.SEARCH,
            label = "Scan Stream",
            tag = "tablet_nav_scan",
            onClick = { setTab(BabuuuuTab.SEARCH) }
        )
        NavigationIconExpanded(
            icon = Icons.Default.Add,
            selected = currentTab == BabuuuuTab.CREATE,
            label = "Transmit Chronic",
            tag = "tablet_nav_create",
            onClick = { setTab(BabuuuuTab.CREATE) }
        )
        NavigationIconExpanded(
            icon = Icons.Default.Email,
            selected = currentTab == BabuuuuTab.MESSAGES,
            label = "Handshake Relay",
            tag = "tablet_nav_messages",
            onClick = { setTab(BabuuuuTab.MESSAGES) }
        )
        NavigationIconExpanded(
            icon = Icons.Default.Notifications,
            selected = currentTab == BabuuuuTab.NOTIFICATIONS,
            label = "System Alerts",
            tag = "tablet_nav_alerts",
            onClick = { setTab(BabuuuuTab.NOTIFICATIONS) }
        )
        NavigationIconExpanded(
            icon = Icons.Default.AccountCircle,
            selected = currentTab == BabuuuuTab.PROFILE,
            label = "Pilot Profile",
            tag = "tablet_nav_profile",
            onClick = { setTab(BabuuuuTab.PROFILE) }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Cyber System Health HUD widget
        GlassMorphismCard(
            cornerRadius = 8.dp,
            borderWidth = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "COMM_RELAY: ACTIVE",
                color = HologramGreen,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ENCRYPTION: QUANTUM",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "BUFFER_LEVEL: 98.4%",
                color = NeonCyan,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun NavigationIconExpanded(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    label: String,
    tag: String,
    onClick: () -> Unit
) {
    val activeBrush = Brush.horizontalGradient(listOf(NeonCyan, SparkViolet))
    val textStyle = if (selected) {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
            .background(Color(0x3B00F2FE))
    } else {
        Modifier.fillMaxWidth()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = textStyle
            .testTag(tag)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) NeonCyan else Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            color = if (selected) GhostWhite else Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

// Expanded Right Sidebar helper column
@Composable
fun RightSidebarExpanded(
    synchronizedNodes: Map<String, Boolean>,
    onNodeClick: (String, Boolean) -> Unit,
    onHashtagClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(Color(0xD2060913))
            .padding(18.dp)
            .safeDrawingPadding()
    ) {
        // Trending Nexus Header
        Text(
            text = "Trending Nexus".uppercase(),
            color = NeonCyan,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Suggestion 1
        NexusSuggestionRow(
            username = "Cypher_Punk",
            handle = "cypher.99",
            avatarColor = 1,
            isSync = synchronizedNodes["cypher.99"] ?: false,
            onToggle = { onNodeClick("cypher.99", !it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Suggestion 2
        NexusSuggestionRow(
            username = "Neon_Aurora",
            handle = "aurora.glow",
            avatarColor = 2,
            isSync = synchronizedNodes["aurora.glow"] ?: false,
            onToggle = { onNodeClick("aurora.glow", !it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Suggestion 3
        NexusSuggestionRow(
            username = "Matrix_Run",
            handle = "runner.zero",
            avatarColor = 4,
            isSync = synchronizedNodes["runner.zero"] ?: false,
            onToggle = { onNodeClick("runner.zero", !it) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Trending data stream tags
        Text(
            text = "Global Data Streams".uppercase(),
            color = SparkViolet,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val hashtagList = listOf(
            "#SubspaceFlares" to "14.2 TB Pulsed",
            "#CosmicCode" to "8.9 TB Pulsed",
            "#ObsidianGrid" to "5.4 TB Pulsed",
            "#BabuuuuLaunch" to "28.1 TB Pulsed"
        )

        hashtagList.forEach { (tag, bandwidth) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onHashtagClick(tag) }
                    .padding(vertical = 8.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = tag,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "BANDWIDTH: $bandwidth",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Trending",
                    tint = TerminalGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun NexusSuggestionRow(
    username: String,
    handle: String,
    avatarColor: Int,
    isSync: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x3B111827), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HolographicAvatar(colorIndex = avatarColor, size = 36.dp, online = true)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = username,
                color = GhostWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "@$handle",
                color = TerminalText,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        Text(
            text = if (isSync) "CONNECTED" else "CONNECT",
            color = if (isSync) TerminalGreen else NeonCyan,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .clickable { onToggle(isSync) }
                .border(
                    width = 1.dp,
                    color = if (isSync) HologramGreen else NeonCyan,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 4.dp)
        )
    }
}

// ---------------- HOME FEED SCREEN TAB ----------------
@Composable
fun HomeFeedTab(
    viewModel: BabuuuuViewModel,
    chronicles: List<Chronic>,
    synchronizedNodes: Map<String, Boolean>,
    onNodeClick: (String, Boolean) -> Unit,
    onTransmitShare: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Horizontal quick node story lines
            Text(
                text = "Synchronized Nodes".uppercase(),
                color = TerminalText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                HolographicAvatar(colorIndex = 1, size = 48.dp, online = true)
                HolographicAvatar(colorIndex = 2, size = 48.dp, online = true)
                HolographicAvatar(colorIndex = 3, size = 48.dp, online = false)
                HolographicAvatar(colorIndex = 4, size = 48.dp, online = true)
                HolographicAvatar(colorIndex = 0, size = 48.dp, online = true)
            }
        }

        if (chronicles.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Empty",
                        tint = SparkViolet.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "NO CHRONICLES DECODED",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Modify your scanning filter query or transmit a new chronic from your cockpit terminal.",
                        color = TerminalText,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 6.dp)
                    )
                }
            }
        } else {
            items(chronicles, key = { it.id }) { chronic ->
                ChronicCard(
                    chronic = chronic,
                    onPulse = { viewModel.togglePulse(chronic) },
                    onEchoComment = { viewModel.selectChronicForComments(chronic.id) },
                    onTransmitShare = { onTransmitShare(chronic.text) },
                    onArchive = { viewModel.toggleArchive(chronic) }
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ChronicCard(
    chronic: Chronic,
    onPulse: () -> Unit,
    onEchoComment: () -> Unit,
    onTransmitShare: () -> Unit,
    onArchive: () -> Unit
) {
    GlassMorphismCard(
        cornerRadius = 14.dp,
        glowing = chronic.isPulsed,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chronic_card_${chronic.id}")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HolographicAvatar(colorIndex = chronic.avatarColorIndex, size = 40.dp, online = true)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chronic.username,
                    color = GhostWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "@${chronic.userHandle}",
                        color = TerminalText,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(3.dp).background(Color(0xFF00F2FE), CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = chronic.nodeSector,
                        color = NeonCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Text(
                text = chronic.latency,
                color = TerminalGreen,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chronicle Text Description
        Text(
            text = chronic.text,
            color = Color.White,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Telemetry Media Grid Container visualizer
        HighTechMediaContainer(visualSeed = chronic.visualSeed)

        Spacer(modifier = Modifier.height(14.dp))

        // Interactivity Buttons Panel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pulse (Like) button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onPulse)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = if (chronic.isPulsed) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Pulse",
                    tint = if (chronic.isPulsed) NeonCyan else Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${chronic.pulseCount} PULSE",
                    color = if (chronic.isPulsed) NeonCyan else Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Echo (Comments) expand button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onEchoComment)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = "Echo Comments",
                    tint = SparkViolet,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${chronic.commentsCount} ECHO",
                    color = SparkViolet,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Transmit (Share) button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onTransmitShare)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Transmit",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "TRANSMIT",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Archive (Bookmark) button
            IconButton(onClick = onArchive) {
                Icon(
                    imageVector = if (chronic.isArchived) Icons.Default.Star else Icons.Default.Settings,
                    contentDescription = "Archive",
                    tint = if (chronic.isArchived) NeonCyan else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ---------------- SEARCH SCI-FI TERMINAL TAB ----------------
@Composable
fun SearchTab(
    viewModel: BabuuuuViewModel,
    searchQuery: String,
    chronicles: List<Chronic>
) {
    val filterResults = remember(searchQuery, chronicles) {
        if (searchQuery.isBlank()) chronicles else {
            chronicles.filter {
                it.text.contains(searchQuery, ignoreCase = true) ||
                        it.username.contains(searchQuery, ignoreCase = true) ||
                        it.nodeSector.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Holographic Search Entry
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("scan_input_field"),
            placeholder = {
                Text(
                    text = "ENTER NODE COORDINATES OR USER SECTOR...",
                    color = Color.White.copy(alpha = 0.35f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = NeonCyan)
            },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color.Red)
                    }
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = GhostWhite,
                unfocusedTextColor = GhostWhite,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = TranslucentBorder,
                focusedContainerColor = ObsidianCard,
                unfocusedContainerColor = ObsidianCard
            ),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Scan Results LOG [Sectors: ${filterResults.size}]".uppercase(),
            color = TerminalGreen,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filterResults.isEmpty()) {
                item {
                    Text(
                        text = "SCAN ERROR: No node synchronization matching query: '$searchQuery'. Ensure sector coordinates are within Galactic Grid.",
                        color = Color.Red.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 20.dp)
                    )
                }
            } else {
                items(filterResults, key = { "search_${it.id}" }) { item ->
                    ChronicCard(
                        chronic = item,
                        onPulse = { viewModel.togglePulse(item) },
                        onEchoComment = { viewModel.selectChronicForComments(item.id) },
                        onTransmitShare = { },
                        onArchive = { viewModel.toggleArchive(item) }
                    )
                }
            }
        }
    }
}

// ---------------- CREATE TRANSMISSION Cockpit TERMINAL TAB ----------------
@Composable
fun CreateTab(viewModel: BabuuuuViewModel) {
    var contentText by remember { mutableStateOf("") }
    var sectorText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "COCKPIT TRANSMISSION MODULE".uppercase(),
            color = NeonCyan,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
        )

        GlassMorphismCard(cornerRadius = 14.dp, glowing = true, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "COMM LINK HANDSHAKE: GUEST_PILOT_ACCESS_AUTHORIZED",
                color = TerminalGreen,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Dynamic Sector Location Input
            OutlinedTextField(
                value = sectorText,
                onValueChange = { sectorText = it },
                label = { Text("Sector Source Marker (e.g. Orbit-4, void-9)", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                placeholder = { Text("Default: GRID-TERMINAL-1", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sector_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    focusedLabelColor = NeonCyan,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = TranslucentBorder
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body text entry
            OutlinedTextField(
                value = contentText,
                onValueChange = { contentText = it },
                label = { Text("Enter Space Broadcast Chronicles Vector text...", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                placeholder = { Text("Write something futuristic...", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .testTag("content_text_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    focusedLabelColor = NeonCyan,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = TranslucentBorder
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        viewModel.addChronic(contentText, sectorText)
                        contentText = ""
                        sectorText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.testTag("transmit_chronic_btn")
                ) {
                    GlowButton(text = "Transmit Chronic Now") {
                        if (contentText.isNotBlank()) {
                            viewModel.addChronic(contentText, sectorText)
                            contentText = ""
                            sectorText = ""
                        }
                    }
                }
            }
        }

        // Additional telemetry detail board
        GlassMorphismCard(cornerRadius = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "TRANSMITTER STATUS: READY",
                color = HologramGreen,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ANTENNA RESONATOR RANGE: 50.4 PARSECS",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "CARRIER FREQUENCY: 4.88 GHZ WITH QUANTUM PARITY",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// ---------------- FUTURISTIC MESSAGES CHAT RELAY TAB ----------------
@Composable
fun MessagesTab(viewModel: BabuuuuViewModel) {
    val activeChat by viewModel.activeChatContact.collectAsStateWithLifecycle()
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    var textInput by remember { mutableStateOf("") }

    // List of cybernetic contact points online
    val contactOptions = remember {
        listOf(
            ChatContact("Astro_Decoders", "@astro.dec", "NEBULA-9", "ONLINE", 1, "Receiving subspace signals from Sagittarius A. Handshake required."),
            ChatContact("Core_Guardian", "@guardian.sys", "CORE-Z", "ONLINE", 2, "Security firmware diagnostics ready. Confirm authorized node link."),
            ChatContact("Solar_flare_relay", "@solar.act", "ORBIT-3", "STANDBY", 3, "High activity recorded near solar corona. Ready for telemetry stream.")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (activeChat == null) {
            Text(
                text = "ACTIVE COMMUNICATIONS RELAYS".uppercase(),
                color = NeonCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(contactOptions) { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x56111827), RoundedCornerShape(10.dp))
                            .border(1.dp, TranslucentBorder, RoundedCornerShape(10.dp))
                            .clickable { viewModel.startChatWith(contact) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HolographicAvatar(colorIndex = contact.avatarColorIndex, size = 44.dp, online = contact.status == "ONLINE")
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contact.username,
                                color = GhostWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "SECTOR: ${contact.sector} // ${contact.status}",
                                color = if (contact.status == "ONLINE") TerminalGreen else Color.Yellow,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = contact.initialMessage,
                                color = TerminalText,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Connect", tint = NeonCyan, modifier = Modifier.size(16.dp))
                    }
                }
            }
        } else {
            // Screen active chat session
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.clearActiveChat() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonCyan)
                }
                Spacer(modifier = Modifier.width(8.dp))
                HolographicAvatar(colorIndex = activeChat!!.avatarColorIndex, size = 36.dp, online = true)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = activeChat!!.username,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "SECURE TUNNEL ID: ${activeChat!!.handle}_RESONANT",
                        color = TerminalGreen,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            HorizontalDivider(color = Color(0x3300F2FE), modifier = Modifier.padding(vertical = 12.dp))

            // Chat content area
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (msg.isMe) Alignment.End else Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 10.dp,
                                        topEnd = 10.dp,
                                        bottomStart = if (msg.isMe) 10.dp else 0.dp,
                                        bottomEnd = if (msg.isMe) 0.dp else 10.dp
                                    )
                                )
                                .background(if (msg.isMe) Color(0x5200F2FE) else Color(0x73111827))
                                .border(
                                    1.dp,
                                    if (msg.isMe) NeonCyan.copy(alpha = 0.5f) else TranslucentBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = msg.content,
                                color = GhostWhite,
                                fontSize = 12.sp,
                            )
                        }
                        Text(
                            text = if (msg.isMe) "MEM_LINK_SENDER" else "RELAY_STATION_RCV",
                            fontSize = 8.sp,
                            color = Color.White.copy(alpha = 0.4f),
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chat input row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("TYPE SUB-COMM PULSE...", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = TranslucentBorder
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendChatMessage(textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(listOf(NeonCyan, SparkViolet)),
                            CircleShape
                        )
                        .testTag("submit_chat_btn")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send Message", tint = Color.Black)
                }
            }
        }
    }
}

// ---------------- SYSTEM ALERT DATA STREAM TAB ----------------
@Composable
fun NotificationsTab(viewModel: BabuuuuViewModel) {
    val alerts by viewModel.notifications.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "SOLAR PAC_COMM SYSTEM ALERTS".uppercase(),
            color = NeonCyan,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(alerts) { alert ->
                GlassMorphismCard(cornerRadius = 10.dp, glowing = alert.type == "NEO-BURST") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = alert.type,
                            color = if (alert.type == "NEO-BURST") Color.Red else NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = alert.category,
                            color = TerminalGreen,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = alert.description,
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// ---------------- PROFILE COCKPIT TAB ----------------
@Composable
fun ProfileTab(viewModel: BabuuuuViewModel, allChronicles: List<Chronic>) {
    val userMyList = remember(allChronicles) {
        allChronicles.filter { it.username == "Pilot_User_Guest" }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "COCKPIT PILOT DIRECTORY".uppercase(),
            color = NeonCyan,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Holographic Pilot Card
        GlassMorphismCard(cornerRadius = 14.dp, glowing = true, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HolographicAvatar(colorIndex = 0, size = 64.dp, online = true)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Pilot_User_Guest",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "RANK: SUPREME_COMMANDER_SEC_01",
                        color = TerminalGreen,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "HANDSHAKE ADDRESS: [guest.pilot]",
                        color = TerminalText,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metric tracking hubs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "SYNC_BURSTS", color = TerminalText, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Text(text = "${userMyList.size}", color = NeonCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "SECTOR_LINKS", color = TerminalText, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Text(text = "402.1K", color = SparkViolet, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "ORBIT_PULSE", color = TerminalText, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Text(text = "92%", color = TerminalGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "TRANSMITTED HISTORIC SECTORS [${userMyList.size}]".uppercase(),
            color = TerminalGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Pilot's own posts list
        if (userMyList.isEmpty()) {
            Text(
                text = "NO HISTORIC PACKET BROADCASTS DETECTED. Connect to Create tab to transmit real-time chronicles into the stellar array network.",
                color = TerminalText,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        } else {
            userMyList.forEach { userChronic ->
                ChronicCard(
                    chronic = userChronic,
                    onPulse = { viewModel.togglePulse(userChronic) },
                    onEchoComment = { viewModel.selectChronicForComments(userChronic.id) },
                    onTransmitShare = {},
                    onArchive = { viewModel.toggleArchive(userChronic) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// ---------------- VERITABLE COMMENT MODAL DIALOG / DRAWER ----------------
@Composable
fun EchoCommentSectionDialog(
    chronicId: Int,
    viewModel: BabuuuuViewModel,
    onDismiss: () -> Unit
) {
    val comments by viewModel.currentEchoes.collectAsStateWithLifecycle()
    var commentText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, NeonCyan, RoundedCornerShape(16.dp)),
            color = ObsidianBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header of dialog
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ECHO COMM STREAM // CODE $chronicId".uppercase(),
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close comments", tint = Color.Red)
                    }
                }

                HorizontalDivider(color = Color(0x3300F2FE), modifier = Modifier.padding(vertical = 10.dp))

                // Scroll comments area
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (comments.isEmpty()) {
                        item {
                            Text(
                                text = "SILENT SECTOR: No acoustic echo data parsed yet. Speak first to start connection loop.",
                                color = TerminalText,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 20.dp, horizontal = 10.dp)
                            )
                        }
                    } else {
                        items(comments) { comment ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0x3B1F2937), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${comment.username} [${comment.userHandle}]",
                                        color = NeonCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = comment.sectorSource,
                                        color = TerminalGreen,
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = comment.text,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Add comment row footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Enter echo telemetry...", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("comment_write_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = TranslucentBorder
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                viewModel.addComment(chronicId, commentText)
                                commentText = ""
                            }
                        },
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(listOf(NeonCyan, SparkViolet)),
                                CircleShape
                            )
                            .testTag("submit_comment_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Add comment", tint = Color.Black)
                    }
                }
            }
        }
    }
}

// Simple custom modifier vertical scrolls
@Composable
fun rememberScrollState() = androidx.compose.foundation.rememberScrollState()
