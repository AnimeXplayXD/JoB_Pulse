package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import com.example.model.*
import com.example.theme.LocalThemeRevealController
import com.example.theme.ThemeRevealProvider
import com.example.ui.components.GlassyDock
import com.example.ui.components.JobCardItem
import com.example.ui.components.NotificationPermissionDialog
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JobDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.AppColors
import com.example.ui.theme.MyApplicationTheme
import com.example.util.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Re-export type aliases for backward compatibility with existing tests
typealias JobCategory = com.example.model.JobCategory
typealias Job = com.example.model.Job
typealias CategoryThemeInfo = com.example.model.CategoryThemeInfo

@Immutable
data class AppState(
    val selectedCategory: JobCategory = JobCategory.ALL,
    val selectedState: String = "All States",
    val searchQuery: String = "",
    val allJobs: List<Job> = emptyList(),
    val jobs: List<Job> = emptyList(),
    val bookmarkedJobIds: Set<Int> = emptySet(),
    val showBookmarksOnly: Boolean = false,
    val isLoading: Boolean = false,
    val isDarkTheme: Boolean = true,
    val showAlertsDialog: Boolean = false,
    val currentTab: NavTab = NavTab.HOME,
    val isOffline: Boolean = false,
    val syncMessage: String? = null
)

class MainViewModel(
    private val repository: com.example.data.repository.JobRepository = if (try { JobPulseApp.instance; true } catch (e: Exception) { false }) {
        com.example.data.repository.JobRepositoryProvider.getRepository(JobPulseApp.instance)
    } else {
        object : com.example.data.repository.JobRepository {
            override fun getJobsStream() = kotlinx.coroutines.flow.flowOf(DummyJobs)
            override fun getJobByIdStream(id: Int) = kotlinx.coroutines.flow.flowOf(DummyJobs.find { it.id == id })
            override fun getOrganisationsStream() = kotlinx.coroutines.flow.flowOf(emptyList<com.example.model.Organisation>())
            override fun searchJobsStream(query: String) = kotlinx.coroutines.flow.flowOf(DummyJobs)
            override suspend fun refresh(force: Boolean) = Result.success(Unit)
        }
    }
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppState())
    val uiState: StateFlow<AppState> = _uiState.asStateFlow()

    init {
        observeJobs()
        syncData(force = false)
    }

    private fun observeJobs() {
        viewModelScope.launch {
            repository.getJobsStream().collect { liveJobs ->
                val sourceJobs = if (liveJobs.isEmpty()) DummyJobs else liveJobs
                _uiState.update { state ->
                    state.copy(
                        allJobs = sourceJobs,
                        jobs = filterJobs(
                            sourceJobs = sourceJobs,
                            category = state.selectedCategory,
                            stateName = state.selectedState,
                            query = state.searchQuery,
                            showBookmarksOnly = state.showBookmarksOnly,
                            bookmarks = state.bookmarkedJobIds
                        )
                    )
                }
            }
        }
    }

    private fun syncData(force: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.refresh(force = force)
            _uiState.update { state ->
                if (result.isSuccess) {
                    state.copy(
                        isLoading = false,
                        isOffline = false,
                        syncMessage = null
                    )
                } else {
                    state.copy(
                        isLoading = false,
                        isOffline = true,
                        syncMessage = "Offline Mode • Serving Cached Notices"
                    )
                }
            }
        }
    }

    fun onRefresh() {
        syncData(force = true)
    }

    fun onSelectTab(tab: NavTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun onToggleBookmark(jobId: Int) {
        _uiState.update { state ->
            val newBookmarks = if (state.bookmarkedJobIds.contains(jobId)) {
                state.bookmarkedJobIds - jobId
            } else {
                state.bookmarkedJobIds + jobId
            }
            state.copy(
                bookmarkedJobIds = newBookmarks,
                jobs = filterJobs(
                    sourceJobs = state.allJobs,
                    category = state.selectedCategory,
                    stateName = state.selectedState,
                    query = state.searchQuery,
                    showBookmarksOnly = state.showBookmarksOnly,
                    bookmarks = newBookmarks
                )
            )
        }
    }

    fun onToggleBookmarksView() {
        _uiState.update { state ->
            val newShowBookmarksOnly = !state.showBookmarksOnly
            state.copy(
                showBookmarksOnly = newShowBookmarksOnly,
                currentTab = NavTab.HOME,
                jobs = filterJobs(
                    sourceJobs = state.allJobs,
                    category = state.selectedCategory,
                    stateName = state.selectedState,
                    query = state.searchQuery,
                    showBookmarksOnly = newShowBookmarksOnly,
                    bookmarks = state.bookmarkedJobIds
                )
            )
        }
    }

    fun onToggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun onToggleAlerts(show: Boolean) {
        _uiState.update { it.copy(showAlertsDialog = show) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                jobs = filterJobs(
                    sourceJobs = state.allJobs,
                    category = state.selectedCategory,
                    stateName = state.selectedState,
                    query = query,
                    showBookmarksOnly = state.showBookmarksOnly,
                    bookmarks = state.bookmarkedJobIds
                )
            )
        }
    }

    fun onCategorySelected(category: JobCategory) {
        _uiState.update { state ->
            val newSelectedState = if (category == JobCategory.STATE) state.selectedState else "All States"
            state.copy(
                selectedCategory = category,
                selectedState = newSelectedState,
                jobs = filterJobs(
                    sourceJobs = state.allJobs,
                    category = category,
                    stateName = newSelectedState,
                    query = state.searchQuery,
                    showBookmarksOnly = state.showBookmarksOnly,
                    bookmarks = state.bookmarkedJobIds
                )
            )
        }
    }

    fun onStateSelected(stateName: String) {
        _uiState.update { state ->
            state.copy(
                selectedState = stateName,
                jobs = filterJobs(
                    sourceJobs = state.allJobs,
                    category = state.selectedCategory,
                    stateName = stateName,
                    query = state.searchQuery,
                    showBookmarksOnly = state.showBookmarksOnly,
                    bookmarks = state.bookmarkedJobIds
                )
            )
        }
    }

    private fun filterJobs(
        sourceJobs: List<Job>,
        category: JobCategory,
        stateName: String,
        query: String,
        showBookmarksOnly: Boolean,
        bookmarks: Set<Int>
    ): List<Job> {
        val jobsToFilter = if (sourceJobs.isEmpty()) DummyJobs else sourceJobs
        return jobsToFilter.filter { job ->
            val categoryMatch = if (category == JobCategory.ALL) true else job.category == category
            val stateMatch = if (category == JobCategory.STATE && stateName != "All States") {
                job.state == stateName || job.location.contains(stateName, ignoreCase = true)
            } else {
                true
            }
            val queryMatch = if (query.isBlank()) true else {
                job.title.contains(query, ignoreCase = true) ||
                job.organization.contains(query, ignoreCase = true) ||
                job.level.contains(query, ignoreCase = true) ||
                job.location.contains(query, ignoreCase = true)
            }
            val bookmarkMatch = if (showBookmarksOnly) bookmarks.contains(job.id) else true

            categoryMatch && stateMatch && queryMatch && bookmarkMatch
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            ThemeRevealProvider(
                onToggleTheme = viewModel::onToggleTheme,
                isDarkTheme = uiState.isDarkTheme
            ) {
                MyApplicationTheme(darkTheme = uiState.isDarkTheme) {
                    GovtJobsApp(
                        uiState = uiState,
                        onCategorySelected = viewModel::onCategorySelected,
                        onStateSelected = viewModel::onStateSelected,
                        onSearchQueryChanged = viewModel::onSearchQueryChanged,
                        onBookmarkToggle = viewModel::onToggleBookmark,
                        onToggleBookmarksView = viewModel::onToggleBookmarksView,
                        onToggleTheme = viewModel::onToggleTheme,
                        onToggleAlerts = viewModel::onToggleAlerts,
                        onRefresh = viewModel::onRefresh,
                        onTabSelected = viewModel::onSelectTab
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun GovtJobsApp(
    uiState: AppState,
    onCategorySelected: (JobCategory) -> Unit,
    onStateSelected: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onBookmarkToggle: (Int) -> Unit,
    onToggleBookmarksView: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleAlerts: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    onTabSelected: (NavTab) -> Unit = {}
) {
    val context = LocalContext.current
    val tokens = com.example.ui.theme.LocalAppThemeTokens.current
    val isDark = tokens.isDark
    val bgColor = tokens.background
    val revealController = LocalThemeRevealController.current
    val wave = com.example.ui.components.LocalThemeWave.current
    var themeButtonCenter by remember { mutableStateOf<Offset?>(null) }
    val topBarColor = tokens.surface
    val onTopBarColor = tokens.textPrimary

    // Distinct scroll states for each tab
    val homeListState = rememberLazyListState()
    val feedListState = rememberLazyListState()
    val searchListState = rememberLazyListState()
    val accountListState = rememberLazyListState()

    // Auto-hiding Dock state driven by nested scroll detection
    var isDockVisible by remember { mutableStateOf(true) }
    
    // Lifted state for double-tap job detail transition
    var selectedJobForDetail by remember { mutableStateOf<Job?>(null) }

    // Navigation back stack for fluid, non-destructive system back gestures
    val tabBackStack = remember { mutableStateListOf(NavTab.HOME) }

    val handleTabSelection: (NavTab) -> Unit = { tab ->
        if (tab != uiState.currentTab) {
            if (tab == NavTab.HOME) {
                tabBackStack.clear()
                tabBackStack.add(NavTab.HOME)
            } else {
                tabBackStack.remove(tab)
                tabBackStack.add(tab)
            }
            onTabSelected(tab)
        }
    }

    // Android System Gesture / Back Handler
    val canHandleBack = selectedJobForDetail != null || uiState.showBookmarksOnly || tabBackStack.size > 1
    BackHandler(enabled = canHandleBack) {
        when {
            selectedJobForDetail != null -> {
                selectedJobForDetail = null
            }
            uiState.showBookmarksOnly -> {
                onToggleBookmarksView()
            }
            tabBackStack.size > 1 -> {
                tabBackStack.removeAt(tabBackStack.lastIndex)
                val previousTab = tabBackStack.last()
                onTabSelected(previousTab)
            }
        }
    }

    // Check whether first-open notification permission popup should be displayed
    LaunchedEffect(Unit) {
        if (NotificationHelper.shouldShowFirstOpenPrompt(context)) {
            onToggleAlerts(true)
        }
    }

    // Contextual Notification Permission Launcher for Android 13+ (API 33+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        NotificationHelper.markFirstOpenPromptShown(context)
        if (isGranted) {
            NotificationHelper.postSubscriptionConfirmedNotification(context)
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (selectedJobForDetail != null) return Offset.Zero // keep dock hidden in detail
                if (available.y < -12f) {
                    isDockVisible = false
                } else if (available.y > 12f) {
                    isDockVisible = true
                }
                return Offset.Zero
            }
        }
    }

    val screenTitle = when (uiState.currentTab) {
        NavTab.HOME -> if (uiState.showBookmarksOnly) "Saved Jobs" else "JobPulse"
        NavTab.FEED -> "Live Announcements"
        NavTab.SEARCH -> "Targeted Search"
        NavTab.ACCOUNT -> "Aspirant Profile"
    }

    // Wrap in solid background container to eliminate transparent crossfade artifacts
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = selectedJobForDetail,
                label = "main_or_detail",
                transitionSpec = {
                    fadeIn(animationSpec = tween(320, easing = FastOutSlowInEasing)) togetherWith
                    fadeOut(animationSpec = tween(320, easing = FastOutSlowInEasing))
                }
            ) { jobDetail ->
                if (jobDetail != null) {
                    JobDetailScreen(
                        job = jobDetail,
                        onBack = { selectedJobForDetail = null },
                        isBookmarked = uiState.bookmarkedJobIds.contains(jobDetail.id),
                        onBookmarkToggle = { onBookmarkToggle(jobDetail.id) },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@AnimatedContent
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.nestedScroll(nestedScrollConnection),
                        containerColor = bgColor,
                        topBar = {
                            TopAppBar(
                                title = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        if (uiState.currentTab == NavTab.HOME && !uiState.showBookmarksOnly) {
                                            com.example.ui.components.JobPulseLogo(symbolSize = 28.dp, textSize = 22.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            com.example.ui.components.JobPulseBadge(
                                                text = if (uiState.isOffline) "CACHED" else "LIVE",
                                                isLive = !uiState.isOffline
                                            )
                                        } else {
                                            Column {
                                                Text(
                                                    text = screenTitle,
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = onTopBarColor
                                                )
                                                if (uiState.showBookmarksOnly) {
                                                    Text(
                                                        text = "${uiState.jobs.size} saved opportunities",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = tokens.textTertiary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = tokens.background,
                                    titleContentColor = onTopBarColor
                                ),
                                actions = {
                                    com.example.ui.components.LiquidGlassBox(
                                        shape = RoundedCornerShape(tokens.dockRadius),
                                        elevation = 2.dp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
                                        ) {
                                            IconButton(
                                                onClick = { onToggleAlerts(true) },
                                                modifier = Modifier.size(38.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Notifications,
                                                    contentDescription = "Alerts",
                                                    tint = onTopBarColor,
                                                    modifier = Modifier.size(19.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = onToggleBookmarksView,
                                                modifier = Modifier.size(38.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (uiState.showBookmarksOnly) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                                    contentDescription = "Bookmarks",
                                                    tint = if (uiState.showBookmarksOnly) tokens.primary else onTopBarColor,
                                                    modifier = Modifier.size(19.dp)
                                                )
                                            }
                                            val themeIconTargetDark = if (wave.isWaveActive) wave.toDark else isDark
                                            val themeIconRotation by animateFloatAsState(
                                                targetValue = if (themeIconTargetDark) 0f else 180f,
                                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                                                label = "theme_icon_rot"
                                            )
                                            val themeIconScale by animateFloatAsState(
                                                targetValue = if (wave.isWaveActive) {
                                                    if (wave.progress < 0.22f) 0.84f else 1.10f
                                                } else {
                                                    if (isDark) 1f else 1.05f
                                                },
                                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                                label = "theme_icon_scale"
                                            )

                                            IconButton(
                                                onClick = {
                                                    revealController.reveal(themeButtonCenter)
                                                },
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .onGloballyPositioned { coordinates ->
                                                        val pos = coordinates.positionInRoot()
                                                        val size = coordinates.size
                                                        themeButtonCenter = Offset(
                                                            pos.x + size.width / 2f,
                                                            pos.y + size.height / 2f
                                                        )
                                                    }
                                            ) {
                                                Icon(
                                                    imageVector = if (themeIconTargetDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                                    contentDescription = "Theme",
                                                    tint = onTopBarColor,
                                                    modifier = Modifier
                                                        .size(19.dp)
                                                        .graphicsLayer {
                                                            rotationZ = themeIconRotation
                                                            scaleX = themeIconScale
                                                            scaleY = themeIconScale
                                                        }
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .background(bgColor)
                        ) {
                            Crossfade(
                                targetState = uiState.currentTab,
                                animationSpec = tween(220),
                                label = "tab_content_transition"
                            ) { activeTab ->
                                when (activeTab) {
                                    NavTab.HOME -> {
                                        HomeScreen(
                                            listState = homeListState,
                                            searchQuery = uiState.searchQuery,
                                            onSearchQueryChanged = onSearchQueryChanged,
                                            selectedCategory = uiState.selectedCategory,
                                            onCategorySelected = onCategorySelected,
                                            selectedState = uiState.selectedState,
                                            onStateSelected = onStateSelected,
                                            jobs = uiState.jobs,
                                            bookmarkedJobIds = uiState.bookmarkedJobIds,
                                            onBookmarkToggle = onBookmarkToggle,
                                            isLoading = uiState.isLoading,
                                            showBookmarksOnly = uiState.showBookmarksOnly,
                                            onRefresh = onRefresh,
                                            onNavigateToSearch = { handleTabSelection(NavTab.SEARCH) },
                                            onJobDoubleTap = { selectedJobForDetail = it },
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            animatedVisibilityScope = this@AnimatedContent
                                        )
                                    }
                                    NavTab.FEED -> {
                                        FeedScreen(listState = feedListState)
                                    }
                                    NavTab.SEARCH -> {
                                        SearchScreen(
                                            listState = searchListState,
                                            bookmarkedIds = uiState.bookmarkedJobIds,
                                            onToggleBookmark = onBookmarkToggle,
                                            onJobDoubleTap = { selectedJobForDetail = it },
                                            allJobs = uiState.allJobs.ifEmpty { DummyJobs },
                                            sharedTransitionScope = this@SharedTransitionLayout,
                                            animatedVisibilityScope = this@AnimatedContent
                                        )
                                    }
                                    NavTab.ACCOUNT -> {
                                        AccountScreen(
                                            listState = accountListState,
                                            bookmarkedCount = uiState.bookmarkedJobIds.size,
                                            onViewBookmarks = onToggleBookmarksView,
                                            isDarkTheme = uiState.isDarkTheme,
                                            onToggleTheme = onToggleTheme
                                        )
                                    }
                                }
                            }

                            // Floating Glassy Dock
                            GlassyDock(
                                currentTab = uiState.currentTab,
                                onTabSelected = { selectedTab ->
                                    isDockVisible = true
                                    handleTabSelection(selectedTab)
                                },
                                isVisible = isDockVisible,
                                isDarkTheme = uiState.isDarkTheme,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }
            }
            
            // Job Alert Subscription & First-Open Permission Dialog
            if (uiState.showAlertsDialog) {
                NotificationPermissionDialog(
                    isPermissionGranted = NotificationHelper.hasNotificationPermission(context),
                    onEnableClick = {
                        NotificationHelper.markFirstOpenPromptShown(context)
                        onToggleAlerts(false)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (NotificationHelper.hasNotificationPermission(context)) {
                                NotificationHelper.postSubscriptionConfirmedNotification(context)
                            } else {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        } else {
                            NotificationHelper.postSubscriptionConfirmedNotification(context)
                        }
                    },
                    onDismissRequest = {
                        NotificationHelper.markFirstOpenPromptShown(context)
                        onToggleAlerts(false)
                    },
                    onSendSampleAlert = {
                        NotificationHelper.postSubscriptionConfirmedNotification(context)
                    },
                    onOpenSettings = {
                        NotificationHelper.openNotificationSettings(context)
                    }
                )
            }
        }
    }
}

// Backward-compatible delegate
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JobCard(job: Job, isBookmarked: Boolean, onBookmarkToggle: () -> Unit) {
    JobCardItem(job = job, isBookmarked = isBookmarked, onBookmarkToggle = onBookmarkToggle)
}
