package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.components.JobCardItem
import com.example.ui.components.JobCardSkeleton
import com.example.ui.theme.LocalAppThemeTokens

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    listState: LazyListState,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    selectedCategory: JobCategory,
    onCategorySelected: (JobCategory) -> Unit,
    selectedState: String,
    onStateSelected: (String) -> Unit,
    jobs: List<Job>,
    bookmarkedJobIds: Set<Int>,
    onBookmarkToggle: (Int) -> Unit,
    isLoading: Boolean,
    showBookmarksOnly: Boolean,
    onRefresh: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onJobDoubleTap: (Job) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val tokens = LocalAppThemeTokens.current
    Column(modifier.fillMaxSize().testTag("home_screen")) {
        Surface(
            color = tokens.surface, shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .clickable(role = Role.Button, onClickLabel = "Search jobs", onClick = onNavigateToSearch)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, null, tint = tokens.textSecondary)
                Spacer(Modifier.width(12.dp))
                Text(searchQuery.ifBlank { "Search jobs and organizations" }, style = MaterialTheme.typography.bodyMedium, color = tokens.textSecondary)
            }
        }
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(JobCategory.entries, key = { it.name }) { category ->
                FilterChip(
                    selectedCategory == category, { onCategorySelected(category) }, { Text(category.displayName) },
                    shape = RoundedCornerShape(50), border = null,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = tokens.background, labelColor = tokens.textSecondary,
                        selectedContainerColor = tokens.surfaceElevated, selectedLabelColor = tokens.textPrimary
                    )
                )
            }
        }
        AnimatedVisibility(selectedCategory == JobCategory.STATE) {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(IndianStates, key = { it }) { state ->
                    FilterChip(selectedState == state, { onStateSelected(state) }, { Text(state) }, shape = RoundedCornerShape(50), border = null)
                }
            }
        }
        PullToRefreshBox(isLoading && jobs.isNotEmpty(), onRefresh, Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()
            ) {
                if (isLoading && jobs.isEmpty()) {
                    items(4, contentType = { "skeleton" }) { JobCardSkeleton() }
                } else if (jobs.isEmpty()) {
                    item {
                        Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(if (showBookmarksOnly) "Your saved jobs appear here" else "No jobs found", style = MaterialTheme.typography.titleLarge, color = tokens.textPrimary)
                            Text(if (showBookmarksOnly) "Save an opportunity with its bookmark button." else "Try another category or refresh for updates.", style = MaterialTheme.typography.bodyMedium, color = tokens.textSecondary)
                        }
                    }
                } else {
                    items(jobs, key = { it.id }, contentType = { "job_card" }) { job ->
                        JobCardItem(
                            job, job.id in bookmarkedJobIds, { onBookmarkToggle(job.id) },
                            onOpenDetails = { onJobDoubleTap(job) },
                            sharedTransitionScope = sharedTransitionScope, animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                }
            }
        }
    }
}
