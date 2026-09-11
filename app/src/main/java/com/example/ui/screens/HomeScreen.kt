package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IndianStates
import com.example.model.Job
import com.example.model.JobCategory
import com.example.ui.components.JobCardItem
import com.example.ui.components.JobCardSkeleton
import com.example.ui.theme.AppColors

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
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen")
    ) {
        // Quick Search Bar - Routes seamlessly to Search screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    if (isDark) AppColors.DarkBorder else AppColors.LightBorder,
                    RoundedCornerShape(16.dp)
                )
                .background(if (isDark) AppColors.DarkSurfaceElevated else AppColors.LightSurfaceElevated)
                .clickable { onNavigateToSearch() }
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Entry",
                    tint = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (searchQuery.isNotBlank()) searchQuery else "Search exam, post, quota or department...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (searchQuery.isNotBlank()) {
                        if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                    } else {
                        if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextTertiary
                    },
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isDark) AppColors.DarkSurfaceSubtle else AppColors.LightSurfaceSubtle
                ) {
                    Text(
                        text = "EXPLORE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(JobCategory.entries) { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category) },
                    label = {
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = if (isDark) AppColors.DarkSurface else AppColors.LightSurface,
                        labelColor = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderWidth = 1.dp
                    )
                )
            }
        }

        // State Filter Chips (when State Govt category is selected)
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
                    val isSelected = selectedState == stateName
                    FilterChip(
                        selected = isSelected,
                        onClick = { onStateSelected(stateName) },
                        label = {
                            Text(
                                text = stateName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (isDark) AppColors.DarkSurfaceSubtle else AppColors.LightSurfaceSubtle,
                            selectedContainerColor = if (isDark) AppColors.DarkSurfaceElevated else AppColors.LightSurfaceElevated,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Jobs List with Pull To Refresh and Shared Bounds
        PullToRefreshBox(
            isRefreshing = isLoading && jobs.isNotEmpty(),
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 100.dp),
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
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (showBookmarksOnly) "No bookmarked jobs saved." else "No recruitment notices found.",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Try switching categories or clearing search filters",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextTertiary
                                )
                            }
                        }
                    }
                } else {
                    items(jobs, key = { it.id }) { job ->
                        JobCardItem(
                            job = job,
                            isBookmarked = bookmarkedJobIds.contains(job.id),
                            onBookmarkToggle = { onBookmarkToggle(job.id) },
                            onDoubleTap = { onJobDoubleTap(job) },
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                }
            }
        }
    }
}
