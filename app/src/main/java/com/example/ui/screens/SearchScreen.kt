package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SearchScreen(
    listState: LazyListState,
    bookmarkedIds: Set<Int>,
    onToggleBookmark: (Int) -> Unit,
    onJobDoubleTap: (Job) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(JobCategory.ALL) }
    var selectedQualification by remember { mutableStateOf("All Qualifications") }
    var onlyApplyActive by remember { mutableStateOf(false) }

    val searchResults = remember(query, selectedCategory, selectedQualification, onlyApplyActive) {
        DummyJobs.filter { job ->
            val matchQuery = if (query.isBlank()) true else {
                job.title.contains(query, ignoreCase = true) ||
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
        // Specific Search Bar Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
                    placeholder = { Text("Search by exam, department, qualification...") },
                    leadingIcon = { Icon(Icons.Default.ManageSearch, contentDescription = "Search") },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Sector Filters
                Text(
                    text = "Sector / Department",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(JobCategory.entries) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Qualification Filters
                Text(
                    text = "Eligibility / Minimum Qualification",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(QualificationLevels) { qual ->
                        FilterChip(
                            selected = selectedQualification == qual,
                            onClick = { selectedQualification = qual },
                            label = { Text(qual) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }

                // Apply Active Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Direct 'Apply Now' Link Enabled Only",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
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
                text = "${searchResults.size} Matching Government Jobs",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
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
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No jobs found for these criteria",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try adjusting qualification or sector filters",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(searchResults, key = { it.id }) { job ->
                    JobCardItem(
                        job = job,
                        isBookmarked = bookmarkedIds.contains(job.id),
                        onBookmarkToggle = { onToggleBookmark(job.id) },
                        onDoubleTap = { onJobDoubleTap(job) }
                    )
                }
            }
        }
    }
}
