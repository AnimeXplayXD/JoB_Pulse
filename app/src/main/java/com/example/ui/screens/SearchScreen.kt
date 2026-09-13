package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.components.JobCardItem
import com.example.ui.theme.LocalAppThemeTokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

private data class SearchFilter(val query: String, val category: JobCategory, val qualification: String, val directOnly: Boolean)
private data class SearchResult(val filter: SearchFilter, val source: List<Job>, val jobs: List<Job>)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchScreen(
    listState: LazyListState,
    bookmarkedIds: Set<Int>,
    onToggleBookmark: (Int) -> Unit,
    onJobDoubleTap: (Job) -> Unit,
    modifier: Modifier = Modifier,
    allJobs: List<Job> = emptyList(),
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(JobCategory.ALL) }
    var qualification by rememberSaveable { mutableStateOf("All Qualifications") }
    var directOnly by rememberSaveable { mutableStateOf(false) }
    var showFilters by rememberSaveable { mutableStateOf(false) }
    val filter = remember(query, category, qualification, directOnly) { SearchFilter(query, category, qualification, directOnly) }
    val result by produceState<SearchResult?>(null, filter, allJobs) {
        value = withContext(Dispatchers.Default) {
            val matches = allJobs.filter { job ->
                ensureActive()
                (filter.query.isBlank() || listOf(job.title, job.organization, job.level, job.location, job.quota, job.minQualification).any { it.contains(filter.query.trim(), true) }) &&
                    (filter.category == JobCategory.ALL || filter.category == job.category) &&
                    (filter.qualification == "All Qualifications" || job.minQualification.equals(filter.qualification, true)) &&
                    (!filter.directOnly || !job.applyUrl.isNullOrBlank())
            }
            SearchResult(filter, allJobs, matches)
        }
    }
    // Effect keys use structural equality; result validity must use the same contract.
    val current = result?.takeIf { it.filter == filter && it.source == allJobs }
    val tokens = LocalAppThemeTokens.current
    Column(modifier.fillMaxSize().testTag("specific_search_screen")) {
        OutlinedTextField(
            value = query, onValueChange = { query = it }, singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).testTag("job_search_query"),
            placeholder = { Text("Search jobs and organizations") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = { if (query.isNotEmpty()) IconButton({ query = "" }) { Icon(Icons.Default.Clear, "Clear search") } },
            shape = RoundedCornerShape(16.dp)
        )
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton({ showFilters = !showFilters }) { Text(if (showFilters) "Hide filters" else "Filters") }
            TextButton({ query = ""; category = JobCategory.ALL; qualification = "All Qualifications"; directOnly = false }) { Text("Reset") }
        }
        AnimatedVisibility(showFilters) {
            Column {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(JobCategory.entries, key = { it.name }) { item ->
                        FilterChip(category == item, { category = item }, { Text(item.displayName) }, border = null, shape = RoundedCornerShape(50))
                    }
                }
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(QualificationLevels, key = { it }) { item ->
                        FilterChip(qualification == item, { qualification = item }, { Text(item) }, border = null, shape = RoundedCornerShape(50))
                    }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("With application links", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Switch(directOnly, { directOnly = it })
                }
            }
        }
        if (current == null) {
            LinearProgressIndicator(Modifier.fillMaxWidth().padding(horizontal = 16.dp))
        } else {
            Text("${current.jobs.size} opportunities", Modifier.padding(horizontal = 20.dp, vertical = 8.dp), color = tokens.textSecondary, style = MaterialTheme.typography.bodySmall)
            LazyColumn(
                state = listState, modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (current.jobs.isEmpty()) item {
                    Text("No matching jobs. Try broadening your search.", Modifier.padding(20.dp), color = tokens.textSecondary)
                }
                items(current.jobs, key = { it.id }, contentType = { "job_card" }) { job ->
                    JobCardItem(
                        job, job.id in bookmarkedIds, { onToggleBookmark(job.id) },
                        onOpenDetails = { onJobDoubleTap(job) },
                        sharedTransitionScope = sharedTransitionScope, animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
        }
    }
}
