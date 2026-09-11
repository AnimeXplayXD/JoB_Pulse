package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.IndianStates
import com.example.model.Job
import com.example.model.JobCategory
import com.example.ui.components.JobCardItem
import com.example.ui.components.JobCardSkeleton

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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen")
    ) {
        // Quick Search Bar - routes to SearchScreen when tapped
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(24.dp))
                .clickable { onNavigateToSearch() }
        ) {
            TextField(
                value = searchQuery,
                onValueChange = {}, // Handled by SearchScreen now
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search jobs, roles, quotas...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                singleLine = true,
                enabled = false, // Disable actual input to make Box capture clicks
                colors = TextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(JobCategory.entries) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        // State Filter Chips (when State Govt category is active)
        AnimatedVisibility(
            visible = selectedCategory == JobCategory.STATE,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(IndianStates) { stateName ->
                    FilterChip(
                        selected = selectedState == stateName,
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

        Spacer(modifier = Modifier.height(6.dp))

        // Jobs List with Pull To Refresh
        PullToRefreshBox(
            isRefreshing = isLoading && jobs.isNotEmpty(),
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (isLoading && jobs.isEmpty()) {
                    items(4) { JobCardSkeleton() }
                } else if (jobs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (showBookmarksOnly) "No bookmarked jobs found." else "No jobs found for the selected criteria.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(jobs, key = { it.id }) { job ->
                        JobCardItem(
                            job = job,
                            isBookmarked = bookmarkedJobIds.contains(job.id),
                            onBookmarkToggle = { onBookmarkToggle(job.id) },
                            onDoubleTap = { onJobDoubleTap(job) }
                        )
                    }
                }
            }
        }
    }
}
