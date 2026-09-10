package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class JobCategory(val displayName: String) {
    ALL("All"),
    CENTRAL("Central Govt"),
    STATE("State Govt"),
    RAILWAY("Railway"),
    BANK("Banking"),
    DEFENSE("Defense")
}

data class Job(
    val id: Int,
    val title: String,
    val category: JobCategory,
    val level: String,
    val salary: String,
    val location: String,
    val seats: Int,
    val quota: String,
    val applyUrl: String?, // Null means no apply now option, show official website
    val noticeUrl: String,
    val officialSiteUrl: String,
    val state: String? = null
)

val IndianStates = listOf(
    "All States", "Uttar Pradesh", "Maharashtra", "Bihar", "West Bengal", "Madhya Pradesh", "Tamil Nadu", "Rajasthan", "Karnataka", "Gujarat", "Andhra Pradesh"
)

val DummyJobs = listOf(
    Job(1, "RRB NTPC Graduate Level", JobCategory.RAILWAY, "Group C", "₹35,400 - ₹1,12,400", "All India", 11558, "UR: 40%, OBC: 27%, SC: 15%, ST: 7.5%, EWS: 10%", "https://rrbcdg.gov.in/apply", "https://rrbcdg.gov.in/notice", "https://rrbcdg.gov.in/"),
    Job(2, "SBI Probationary Officer", JobCategory.BANK, "Officer Scale I", "₹41,960 - ₹63,840", "All India", 2000, "UR: 40.5%, OBC: 27%, SC: 15%, ST: 7.5%, EWS: 10%", "https://sbi.co.in/careers", "https://sbi.co.in/notice", "https://sbi.co.in/"),
    Job(3, "NDA & NA Examination", JobCategory.DEFENSE, "Officer", "₹56,100 - ₹1,77,500", "All India", 400, "Based on Merit", null, "https://upsc.gov.in/notice", "https://upsc.gov.in/"),
    Job(4, "SSC CGL", JobCategory.CENTRAL, "Group B & C", "₹25,500 - ₹1,51,100", "All India", 17727, "UR: 40%, OBC: 27%, SC: 15%, ST: 7.5%, EWS: 10%", "https://ssc.nic.in/apply", "https://ssc.nic.in/notice", "https://ssc.nic.in/"),
    Job(5, "UP Police Constable", JobCategory.STATE, "Group C", "₹21,700 - ₹69,100", "Uttar Pradesh", 60244, "UR: 40%, OBC: 27%, SC: 21%, ST: 2%, EWS: 10%", "https://uppbpb.gov.in/apply", "https://uppbpb.gov.in/notice", "https://uppbpb.gov.in/", "Uttar Pradesh"),
    Job(6, "MPSC Civil Services", JobCategory.STATE, "Class I & II", "₹56,100 - ₹1,77,500", "Maharashtra", 274, "State Govt Quotas apply", null, "https://mpsc.gov.in/notice", "https://mpsc.gov.in/", "Maharashtra"),
    Job(7, "BPSC Combined Competitive Exam", JobCategory.STATE, "Class I & II", "₹53,100 - ₹1,67,800", "Bihar", 346, "State Govt Quotas apply", "https://bpsc.bih.nic.in/apply", "https://bpsc.bih.nic.in/notice", "https://bpsc.bih.nic.in/", "Bihar"),
    Job(8, "IBPS PO", JobCategory.BANK, "Officer Scale I", "₹36,000 - ₹63,840", "All India", 3049, "UR: 40%, OBC: 27%, SC: 15%, ST: 7.5%, EWS: 10%", "https://ibps.in/apply", "https://ibps.in/notice", "https://ibps.in/"),
    Job(9, "Indian Navy Agniveer", JobCategory.DEFENSE, "Sailor", "₹30,000 / month", "All India", 1365, "Based on Merit", "https://joinindiannavy.gov.in/apply", "https://joinindiannavy.gov.in/notice", "https://joinindiannavy.gov.in/"),
    Job(10, "RRB Group D", JobCategory.RAILWAY, "Group D", "₹18,000 - ₹56,900", "All India", 103769, "UR: 40%, OBC: 27%, SC: 15%, ST: 7.5%, EWS: 10%", null, "https://rrbcdg.gov.in/notice", "https://rrbcdg.gov.in/")
)

data class AppState(
    val selectedCategory: JobCategory = JobCategory.ALL,
    val selectedState: String = "All States",
    val searchQuery: String = "",
    val jobs: List<Job> = emptyList(),
    val bookmarkedJobIds: Set<Int> = emptySet(),
    val showBookmarksOnly: Boolean = false,
    val isLoading: Boolean = false,
    val isDarkTheme: Boolean = true,
    val showAlertsDialog: Boolean = false
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
            delay(1500) // Simulate network/refresh delay
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
                    onRefresh = viewModel::onRefresh
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    onRefresh: () -> Unit
) {
    // Smooth transition animations for theme change
    val bgColor by animateColorAsState(targetValue = MaterialTheme.colorScheme.background, animationSpec = tween(500))
    val topBarColor by animateColorAsState(targetValue = MaterialTheme.colorScheme.surface, animationSpec = tween(500))
    val onTopBarColor by animateColorAsState(targetValue = MaterialTheme.colorScheme.onSurface, animationSpec = tween(500))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Govt Jobs LIVE", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bgColor)
        ) {
            // Search Bar
            TextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp)),
                placeholder = { Text("Search jobs, roles...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            // Category Filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(JobCategory.values()) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { onCategorySelected(category) },
                        label = { Text(category.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // State Filter
            AnimatedVisibility(
                visible = uiState.selectedCategory == JobCategory.STATE,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(IndianStates) { stateName ->
                        FilterChip(
                            selected = uiState.selectedState == stateName,
                            onClick = { onStateSelected(stateName) },
                            label = { Text(stateName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Jobs List with Pull to Refresh
            PullToRefreshBox(
                isRefreshing = uiState.isLoading && uiState.jobs.isNotEmpty(),
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Show Skeletons if loading and list is empty
                    if (uiState.isLoading && uiState.jobs.isEmpty()) {
                        items(5) { JobCardSkeleton() }
                    } else if (uiState.jobs.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (uiState.showBookmarksOnly) "No bookmarked jobs found." else "No jobs found for the selected criteria.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(uiState.jobs, key = { it.id }) { job ->
                            JobCard(
                                job = job,
                                isBookmarked = uiState.bookmarkedJobIds.contains(job.id),
                                onBookmarkToggle = { onBookmarkToggle(job.id) }
                            )
                        }
                    }
                }
            }
        }

        if (uiState.showAlertsDialog) {
            AlertDialog(
                onDismissRequest = { onToggleAlerts(false) },
                title = { Text("Set Job Alerts") },
                text = { Text("Would you like to receive notifications when new government jobs match your current filters?") },
                confirmButton = {
                    TextButton(onClick = { onToggleAlerts(false) }) { Text("Subscribe") }
                },
                dismissButton = {
                    TextButton(onClick = { onToggleAlerts(false) }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun JobCardSkeleton() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun JobCard(job: Job, isBookmarked: Boolean, onBookmarkToggle: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val themeInfo = getThemeInfoForCategory(job.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(themeInfo.gradient)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = themeInfo.icon,
                            contentDescription = job.category.displayName,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Title and Sector
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = job.category.displayName.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = job.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = if (expanded) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Bookmark Icon
                    IconButton(
                        onClick = { onBookmarkToggle() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Quick Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(icon = Icons.Default.Work, text = job.level)
                    InfoItem(icon = Icons.Default.LocationOn, text = job.location)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                InfoItem(icon = Icons.Default.Paid, text = job.salary)
                
                Spacer(modifier = Modifier.height(12.dp))
                InfoItem(icon = Icons.Default.People, text = "${job.seats} Seats Available")

                // Expanded Content
                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        HorizontalDivider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Quota Breakdown",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = job.quota,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (job.applyUrl != null) {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.applyUrl))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = themeInfo.primaryColor
                                    )
                                ) {
                                    Text("Apply Now", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.officialSiteUrl))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = themeInfo.primaryColor
                                    )
                                ) {
                                    Text("Official Site", fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.noticeUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                            ) {
                                Text("Notice")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

data class CategoryThemeInfo(
    val primaryColor: Color,
    val gradient: Brush,
    val icon: ImageVector
)

fun getThemeInfoForCategory(category: JobCategory): CategoryThemeInfo {
    return when (category) {
        JobCategory.RAILWAY -> CategoryThemeInfo(
            primaryColor = Color(0xFF1565C0),
            gradient = Brush.linearGradient(listOf(Color(0xFF1976D2), Color(0xFF0D47A1))),
            icon = Icons.Default.DirectionsTransit
        )
        JobCategory.BANK -> CategoryThemeInfo(
            primaryColor = Color(0xFF2E7D32),
            gradient = Brush.linearGradient(listOf(Color(0xFF388E3C), Color(0xFF1B5E20))),
            icon = Icons.Default.AccountBalance
        )
        JobCategory.DEFENSE -> CategoryThemeInfo(
            primaryColor = Color(0xFF455A64),
            gradient = Brush.linearGradient(listOf(Color(0xFF546E7A), Color(0xFF263238))),
            icon = Icons.Default.Security
        )
        JobCategory.STATE -> CategoryThemeInfo(
            primaryColor = Color(0xFFE65100),
            gradient = Brush.linearGradient(listOf(Color(0xFFF57C00), Color(0xFFE65100))),
            icon = Icons.Default.Map
        )
        JobCategory.CENTRAL -> CategoryThemeInfo(
            primaryColor = Color(0xFFC62828),
            gradient = Brush.linearGradient(listOf(Color(0xFFD32F2F), Color(0xFFB71C1C))),
            icon = Icons.Default.Flag
        )
        JobCategory.ALL -> CategoryThemeInfo(
            primaryColor = Color(0xFF673AB7),
            gradient = Brush.linearGradient(listOf(Color(0xFF7E57C2), Color(0xFF4527A0))),
            icon = Icons.Default.Work
        )
    }
}
