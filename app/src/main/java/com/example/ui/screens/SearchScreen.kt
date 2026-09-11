package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.JobCardItem
import com.example.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SearchScreen(
    listState: LazyListState,
    bookmarkedIds: Set<Int>,
    onToggleBookmark: (Int) -> Unit,
    onJobDoubleTap: (Job) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(JobCategory.ALL) }
    var selectedQualification by remember { mutableStateOf("All Qualifications") }
    var onlyApplyActive by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()

    val searchResults = remember(query, selectedCategory, selectedQualification, onlyApplyActive) {
        DummyJobs.filter { job ->
            val matchQuery = if (query.isBlank()) true else {
                job.title.contains(query, ignoreCase = true) ||
                job.organization.contains(query, ignoreCase = true) ||
                job.level.contains(query, ignoreCase = true) ||
                job.location.contains(query, ignoreCase = true) ||
                job.quota.contains(query, ignoreCase = true)
            }
            val matchCategory = if (selectedCategory == JobCategory.ALL) true else job.category == selectedCategory
            val matchQual = if (selectedQualification == "All Qualifications") true else job.minQualification.equals(selectedQualification, ignoreCase = true)
            val matchApply = if (onlyApplyActive) job.applyUrl != null else true

            matchQuery && matchCategory && matchQual && matchApply
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("specific_search_screen")
    ) {
        // Search Header Surface
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (isDark) AppColors.DarkSurface else AppColors.LightSurface,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(
                0.5.dp,
                if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
            )
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                // Search Input Field
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            if (isDark) AppColors.DarkBorder else AppColors.LightBorder,
                            RoundedCornerShape(14.dp)
                        ),
                    placeholder = {
                        Text(
                            "Search by exam, department, role, or qualification...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ManageSearch,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search query")
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = if (isDark) AppColors.DarkSurfaceElevated else AppColors.LightSurfaceElevated,
                        unfocusedContainerColor = if (isDark) AppColors.DarkSurfaceElevated else AppColors.LightSurfaceElevated,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sector Filter Chips
                Text(
                    text = "RECRUITMENT SECTOR",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary,
                    letterSpacing = 0.5.sp
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 5.dp)
                ) {
                    items(JobCategory.entries) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = { Text(category.displayName, style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = if (isDark) AppColors.DarkSurfaceElevated else AppColors.LightSurfaceElevated,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Minimum Qualification Filter Chips
                Text(
                    text = "MINIMUM QUALIFICATION",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary,
                    letterSpacing = 0.5.sp
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 5.dp)
                ) {
                    items(QualificationLevels) { qual ->
                        val isSelected = selectedQualification == qual
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedQualification = qual },
                            label = { Text(qual, style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = if (isDark) AppColors.DarkSurfaceElevated else AppColors.LightSurfaceElevated,
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Direct Apply Filter Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Show Direct 'Apply Now' Links Only",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                    )
                    Switch(
                        checked = onlyApplyActive,
                        onCheckedChange = { onlyApplyActive = it }
                    )
                }
            }
        }

        // Result Count Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${searchResults.size} Matching Opportunities",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
            )

            if (selectedCategory != JobCategory.ALL || selectedQualification != "All Qualifications" || query.isNotEmpty() || onlyApplyActive) {
                TextButton(
                    onClick = {
                        query = ""
                        selectedCategory = JobCategory.ALL
                        selectedQualification = "All Qualifications"
                        onlyApplyActive = false
                    }
                ) {
                    Text("Reset Filters", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Search Results List
        if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextTertiary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No matching vacancies found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try broadening your sector or qualification filters",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextTertiary
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(searchResults, key = { it.id }) { job ->
                    JobCardItem(
                        job = job,
                        isBookmarked = bookmarkedIds.contains(job.id),
                        onBookmarkToggle = { onToggleBookmark(job.id) },
                        onDoubleTap = { onJobDoubleTap(job) },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
        }
    }
}
