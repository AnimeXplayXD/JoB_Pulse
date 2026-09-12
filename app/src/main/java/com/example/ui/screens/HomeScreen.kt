package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.SearchOff
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
import com.example.ui.components.LiquidGlassBox
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
        ) {
            LiquidGlassBox(
                shape = RoundedCornerShape(16.dp),
                elevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSearch() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Entry",
                        tint = tokens.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) searchQuery else "Search exam, post, quota or department...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (searchQuery.isNotBlank()) tokens.textPrimary else tokens.textTertiary,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = tokens.primary.copy(alpha = if (tokens.isDark) 0.18f else 0.10f)
                    ) {
                        Text(
                            text = "EXPLORE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = tokens.primary,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(JobCategory.entries, key = { it.name }) { category ->
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
                    shape = RoundedCornerShape(tokens.chipRadius),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = tokens.surfaceElevated,
                        labelColor = tokens.textSecondary,
                        selectedContainerColor = tokens.primary,
                        selectedLabelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = tokens.borderSubtle,
                        selectedBorderColor = tokens.primary,
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
                items(IndianStates, key = { it }) { stateName ->
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
                        shape = RoundedCornerShape(tokens.chipRadius),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = tokens.surfaceSubtle,
                            selectedContainerColor = tokens.surfaceElevated,
                            selectedLabelColor = tokens.primary
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
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 110.dp),
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
                                .padding(vertical = 48.dp, horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(tokens.surfaceElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.SearchOff,
                                        contentDescription = null,
                                        tint = tokens.textTertiary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Text(
                                    text = if (showBookmarksOnly) "No bookmarked jobs saved." else "No recruitment notices found.",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = tokens.textPrimary
                                )
                                Text(
                                    text = if (showBookmarksOnly) "Bookmark opportunities from the feed to view them here." else "Try switching categories or clearing search filters.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textTertiary
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = jobs,
                        key = { it.id },
                        contentType = { "job_card" }
                    ) { job ->
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
