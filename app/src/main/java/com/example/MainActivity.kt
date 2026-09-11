package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.*
import com.example.ui.components.GlassyDock
import com.example.ui.components.JobCardItem
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JobDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.MyApplicationTheme
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
    val jobs: List<Job> = emptyList(),
    val bookmarkedJobIds: Set<Int> = emptySet(),
    val showBookmarksOnly: Boolean = false,
    val isLoading: Boolean = false,
    val isDarkTheme: Boolean = true,
    val showAlertsDialog: Boolean = false,
    val currentTab: NavTab = NavTab.HOME
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppState())
    val uiState: StateFlow<AppState> = _uiState.asStateFlow()

    init {
        loadJobs(isRefresh = false)
    }

    private fun loadJobs(isRefresh: Boolean) {
        viewModelScope.launch {
            if (!isRefresh) {
                _uiState.update { it.copy(isLoading = true, jobs = emptyList()) }
            } else {
                _uiState.update { it.copy(isLoading = true) }
            }
            delay(1200) // Realistic network refresh delay
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    jobs = filterJobs(
                        state.selectedCategory,
                        state.selectedState,
                        state.searchQuery,
                        state.showBookmarksOnly,
                        state.bookmarkedJobIds
                    )
                )
            }
        }
    }

    fun onRefresh() {
        loadJobs(isRefresh = true)
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
                    state.selectedCategory,
                    state.selectedState,
                    state.searchQuery,
                    state.showBookmarksOnly,
                    newBookmarks
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
                    state.selectedCategory,
                    state.selectedState,
                    state.searchQuery,
                    newShowBookmarksOnly,
                    state.bookmarkedJobIds
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
                    state.selectedCategory,
                    state.selectedState,
                    query,
                    state.showBookmarksOnly,
                    state.bookmarkedJobIds
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
                    category,
                    newSelectedState,
                    state.searchQuery,
                    state.showBookmarksOnly,
                    state.bookmarkedJobIds
                )
            )
        }
    }

    fun onStateSelected(stateName: String) {
        _uiState.update { state ->
            state.copy(
                selectedState = stateName,
                jobs = filterJobs(
                    state.selectedCategory,
                    stateName,
                    state.searchQuery,
                    state.showBookmarksOnly,
                    state.bookmarkedJobIds
                )
            )
        }
    }

    private fun filterJobs(
        category: JobCategory,
        stateName: String,
        query: String,
        showBookmarksOnly: Boolean,
        bookmarks: Set<Int>
    ): List<Job> {
        return DummyJobs.filter { job ->
            val categoryMatch = if (category == JobCategory.ALL) true else job.category == category
            val stateMatch = if (category == JobCategory.STATE && stateName != "All States") {
                job.state == stateName
            } else {
                true
            }
            val queryMatch = if (query.isBlank()) true else {
                job.title.contains(query, ignoreCase = true) || job.level.contains(query, ignoreCase = true)
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
    // Use theme colors directly — animateColorAsState on every frame is expensive
    // and the MaterialTheme transition itself already provides a smooth switch.
    val bgColor = MaterialTheme.colorScheme.background
    val topBarColor = MaterialTheme.colorScheme.surface
    val onTopBarColor = MaterialTheme.colorScheme.onSurface

    // Separate scroll states for each screen destination
    val homeListState = rememberLazyListState()
    val feedListState = rememberLazyListState()
    val searchListState = rememberLazyListState()
    val accountListState = rememberLazyListState()

    // Auto-hiding Dock state driven by nested scroll detection
    var isDockVisible by remember { mutableStateOf(true) }
    
    // Lifted state for double-tap job detail transition
    var selectedJobForDetail by remember { mutableStateOf<Job?>(null) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (selectedJobForDetail != null) return Offset.Zero // disable hiding when in detail
                // Hide dock when scrolling down; reveal dock when scrolling up
                if (available.y < -12f) {
                    isDockVisible = false
                } else if (available.y > 12f) {
                    isDockVisible = true
                }
                return Offset.Zero
            }
        }
    }

    // Top Bar title depends on active tab
    val screenTitle = when (uiState.currentTab) {
        NavTab.HOME -> if (uiState.showBookmarksOnly) "Saved Jobs" else "Govt Jobs LIVE"
        NavTab.FEED -> "Live Announcements"
        NavTab.SEARCH -> "Targeted Search"
        NavTab.ACCOUNT -> "Aspirant Profile"
    }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = selectedJobForDetail,
            label = "main_or_detail",
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            }
        ) { jobDetail ->
            if (jobDetail != null) {
                // We show the Job Detail Screen
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
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(screenTitle, fontWeight = FontWeight.Bold)
                                    if (uiState.currentTab == NavTab.HOME && !uiState.showBookmarksOnly) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.error
                                        ) {
                                            Text(
                                                text = "LIVE",
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = topBarColor,
                                titleContentColor = onTopBarColor
                            ),
                            actions = {
                                IconButton(onClick = { onToggleAlerts(true) }) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = onTopBarColor)
                                }
                                IconButton(onClick = onToggleBookmarksView) {
                                    Icon(
                                        imageVector = if (uiState.showBookmarksOnly) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmarks",
                                        tint = onTopBarColor
                                    )
                                }
                                IconButton(onClick = onToggleTheme) {
                                    Icon(
                                        imageVector = if (uiState.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = "Theme",
                                        tint = onTopBarColor
                                    )
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
                        // Crossfade is much cheaper than AnimatedContent for full-screen tab
                        // switches — it only cross-fades alpha instead of measuring both screens.
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
                                        onNavigateToSearch = { onTabSelected(NavTab.SEARCH) },
                                        onJobDoubleTap = { selectedJobForDetail = it }
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
                                        onJobDoubleTap = { selectedJobForDetail = it }
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

                        // Floating Glassy Dock (Not attached to bottom, auto-hides on scroll down)
                        GlassyDock(
                            currentTab = uiState.currentTab,
                            onTabSelected = { selectedTab ->
                                isDockVisible = true
                                onTabSelected(selectedTab)
                            },
                            isVisible = isDockVisible,
                            isDarkTheme = uiState.isDarkTheme,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
        
        // Job Alert Dialog
        if (uiState.showAlertsDialog) {
            AlertDialog(
                onDismissRequest = { onToggleAlerts(false) },
                title = { Text("Set Live Job Alerts") },
                text = { Text("Receive instant push notifications whenever a new all-India or state government quota vacancy matches your preferences?") },
                confirmButton = {
                    TextButton(onClick = { onToggleAlerts(false) }) { Text("Subscribe Now") }
                },
                dismissButton = {
                    TextButton(onClick = { onToggleAlerts(false) }) { Text("Cancel") }
                }
            )
        }
    }
}

// Backward-compatible delegates
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JobCard(job: Job, isBookmarked: Boolean, onBookmarkToggle: () -> Unit) {
    JobCardItem(job = job, isBookmarked = isBookmarked, onBookmarkToggle = onBookmarkToggle)
}
