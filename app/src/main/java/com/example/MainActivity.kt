package com.example

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AndroidUserPreferences
import com.example.model.*
import com.example.theme.LocalThemeRevealController
import com.example.theme.ThemeRevealProvider
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.LocalAppThemeTokens
import com.example.ui.theme.MyApplicationTheme
import com.example.util.NotificationHelper
import kotlinx.coroutines.launch

typealias JobCategory = com.example.model.JobCategory
typealias Job = com.example.model.Job
typealias CategoryThemeInfo = com.example.model.CategoryThemeInfo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.setupNotificationChannels(this)
        val preferences = AndroidUserPreferences(applicationContext)
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                require(modelClass.isAssignableFrom(MainViewModel::class.java))
                return MainViewModel(preferences = preferences, savedStateHandle = extras.createSavedStateHandle()) as T
            }
        }
        setContent {
            val model: MainViewModel = viewModel(factory = factory)
            val state by model.uiState.collectAsStateWithLifecycle()
            ThemeRevealProvider(model::onToggleTheme, state.isDarkTheme) {
                MyApplicationTheme(state.isDarkTheme) {
                    GovtJobsApp(
                        state, model::onCategorySelected, model::onStateSelected,
                        model::onSearchQueryChanged, model::onToggleBookmark,
                        model::onToggleBookmarksView, model::onToggleTheme,
                        model::onToggleAlerts, model::onRefresh, model::onSelectTab
                    )
                }
            }
        }
    }
}

private tailrec fun Context.activity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.activity()
    else -> null
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
    val tokens = LocalAppThemeTokens.current
    val reveal = LocalThemeRevealController.current
    val home = rememberLazyListState()
    val feed = rememberLazyListState()
    val search = rememberLazyListState()
    val account = rememberLazyListState()
    val savedTabs = rememberSaveableStateHolder()
    var selectedId by rememberSaveable { mutableStateOf<Int?>(null) }
    var dockVisible by remember { mutableStateOf(true) }
    var dockHeightPx by remember { mutableIntStateOf(0) }
    val dockClearance = maxOf(120.dp, with(LocalDensity.current) { dockHeightPx.toDp() } + 16.dp)
    var themeCenter by remember { mutableStateOf<Offset?>(null) }
    var permissionGranted by remember { mutableStateOf(NotificationHelper.hasNotificationPermission(context)) }
    var showEducation by rememberSaveable { mutableStateOf(NotificationHelper.shouldShowFirstOpenPrompt(context)) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        permissionGranted = NotificationHelper.hasNotificationPermission(context)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permissionGranted = granted && NotificationHelper.hasNotificationPermission(context)
        scope.launch {
            snackbar.showSnackbar(if (permissionGranted) "Notification permission enabled. Delivery depends on connected services." else "Notifications remain off. You can change this in settings.")
        }
    }
    val requestAlerts: () -> Unit = {
        showEducation = false
        NotificationHelper.markFirstOpenPromptShown(context)
        if (permissionGranted) {
            NotificationHelper.openNotificationSettings(context)
        } else if (Build.VERSION.SDK_INT >= 33) {
            val activity = context.activity()
            val canExplain = activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS)
            if (NotificationHelper.wasPermissionRequested(context) && !canExplain) {
                NotificationHelper.openNotificationSettings(context)
            } else {
                NotificationHelper.markPermissionRequested(context)
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else NotificationHelper.openNotificationSettings(context)
    }
    LaunchedEffect(uiState.showAlertsDialog) {
        if (uiState.showAlertsDialog) { onToggleAlerts(false); requestAlerts() }
    }
    LaunchedEffect(uiState.currentTab) { dockVisible = true }
    BackHandler(selectedId != null || uiState.showBookmarksOnly || uiState.currentTab != NavTab.HOME) {
        when {
            selectedId != null -> selectedId = null
            uiState.showBookmarksOnly -> onToggleBookmarksView()
            else -> onTabSelected(NavTab.HOME)
        }
    }
    val threshold = with(LocalDensity.current) { 28.dp.toPx() }
    val scroll = remember(threshold, uiState.currentTab) {
        object : NestedScrollConnection {
            var distance = 0f
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (distance * consumed.y < 0) distance = 0f
                distance += consumed.y
                if (distance < -threshold) { dockVisible = false; distance = 0f }
                if (distance > threshold) { dockVisible = true; distance = 0f }
                return Offset.Zero
            }
        }
    }
    Box(Modifier.fillMaxSize().background(tokens.background)) {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = selectedId,
                transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
                label = "job_details"
            ) { detailId ->
                val detailScope = this
                if (detailId != null) {
                    val job = uiState.allJobs.find { it.id == detailId }
                    if (job == null) {
                        Column(Modifier.fillMaxSize().statusBarsPadding().padding(24.dp)) {
                            Text(
                                uiState.cacheError ?: if (!uiState.hasLoadedJobs) "Loading saved notice…" else "This notice is no longer available.",
                                color = tokens.textPrimary
                            )
                            if (uiState.cacheError != null) TextButton(onRefresh) { Text("Retry") }
                            TextButton({ selectedId = null }) { Text("Back to jobs") }
                        }
                    } else JobDetailScreen(
                        job, { selectedId = null }, job.id in uiState.bookmarkedJobIds,
                        { onBookmarkToggle(job.id) }, this@SharedTransitionLayout, detailScope
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.imePadding(),
                        containerColor = tokens.background,
                        snackbarHost = { SnackbarHost(snackbar) },
                        topBar = {
                            TopAppBar(
                                title = { Text(when (uiState.currentTab) {
                                    NavTab.HOME -> if (uiState.showBookmarksOnly) "Saved jobs" else "JobPulse"
                                    NavTab.FEED -> "Updates"
                                    NavTab.SEARCH -> "Search"
                                    NavTab.ACCOUNT -> "Your preferences"
                                }) },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = tokens.background),
                                actions = {
                                    IconButton(requestAlerts) { Icon(Icons.Default.Notifications, "Notification settings") }
                                    IconButton(onToggleBookmarksView) { Icon(Icons.Default.Bookmark, "Saved jobs") }
                                    IconButton(
                                        { reveal.reveal(themeCenter) },
                                        Modifier.onGloballyPositioned {
                                            themeCenter = it.positionInRoot() + Offset(it.size.width / 2f, it.size.height / 2f)
                                        }
                                    ) { Icon(if (uiState.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, "Change theme") }
                                }
                            )
                        }
                    ) { padding ->
                        val layer = rememberGraphicsLayer()
                        val backdrop = remember(layer) { GlassBackdrop(layer) }
                        Box(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).nestedScroll(scroll)) {
                            Column(Modifier.fillMaxSize().recordGlassBackdrop(backdrop).background(tokens.background)) {
                                uiState.cacheError?.let { Text(it, Modifier.padding(horizontal = 20.dp, vertical = 4.dp), color = tokens.textSecondary) }
                                if (BuildConfig.DEBUG) Text("Development data · not verified recruitment information", Modifier.padding(horizontal = 20.dp, vertical = 4.dp), color = tokens.textSecondary, style = MaterialTheme.typography.bodySmall)
                                uiState.syncMessage?.let { Text(it, Modifier.padding(horizontal = 20.dp, vertical = 4.dp), color = tokens.textSecondary, style = MaterialTheme.typography.bodySmall) }
                                if (showEducation && !permissionGranted) {
                                    Column(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                                        Text("Allow notifications when you want reminders. Alert delivery is not connected yet.", style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
                                        Row {
                                            TextButton(requestAlerts) { Text("Enable notifications") }
                                            TextButton({ showEducation = false; NotificationHelper.markFirstOpenPromptShown(context) }) { Text("Not now") }
                                        }
                                    }
                                }
                                CompositionLocalProvider(LocalDockContentPadding provides dockClearance) {
                                Crossfade(uiState.currentTab, Modifier.weight(1f), animationSpec = tween(160), label = "tab_content") { tab ->
                                    savedTabs.SaveableStateProvider(tab.name) {
                                        // Only the active tab owns shared keys during tab crossfades.
                                        val tabSharedScope = if (tab == uiState.currentTab) this@SharedTransitionLayout else null
                                        when (tab) {
                                            NavTab.HOME -> HomeScreen(
                                                home, uiState.searchQuery, onSearchQueryChanged,
                                                uiState.selectedCategory, onCategorySelected, uiState.selectedState, onStateSelected,
                                                uiState.jobs, uiState.bookmarkedJobIds, onBookmarkToggle,
                                                uiState.isLoading || (!uiState.hasLoadedJobs && uiState.cacheError == null) || uiState.isFiltering, uiState.showBookmarksOnly, onRefresh,
                                                { onTabSelected(NavTab.SEARCH) }, { selectedId = it.id },
                                                sharedTransitionScope = tabSharedScope, animatedVisibilityScope = detailScope
                                            )
                                            NavTab.FEED -> FeedScreen(feed)
                                            NavTab.SEARCH -> SearchScreen(
                                                search, uiState.bookmarkedJobIds, onBookmarkToggle, { selectedId = it.id },
                                                allJobs = uiState.allJobs,
                                                sharedTransitionScope = tabSharedScope, animatedVisibilityScope = detailScope
                                            )
                                            NavTab.ACCOUNT -> AccountScreen(account, uiState.bookmarkedJobIds.size, onToggleBookmarksView, uiState.isDarkTheme, onToggleTheme)
                                        }
                                    }
                                }
                                }
                            }
                            CompositionLocalProvider(LocalGlassBackdrop provides backdrop) {
                                val keyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
                                GlassyDock(uiState.currentTab, onTabSelected, dockVisible && !keyboardVisible, uiState.isDarkTheme,
                                    Modifier.align(Alignment.BottomCenter).onSizeChanged { if (it.height > 0) dockHeightPx = it.height })
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JobCard(job: Job, isBookmarked: Boolean, onBookmarkToggle: () -> Unit) {
    JobCardItem(job, isBookmarked, onBookmarkToggle)
}
