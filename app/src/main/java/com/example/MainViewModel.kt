package com.example

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.UserPreferences
import com.example.data.repository.JobRepository
import com.example.data.repository.JobRepositoryProvider
import com.example.model.NavTab
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job as CoroutineJob
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Immutable
data class AppState(
    val selectedCategory: JobCategory = JobCategory.ALL,
    val selectedState: String = "All States",
    val searchQuery: String = "",
    val allJobs: List<Job> = emptyList(),
    val jobs: List<Job> = emptyList(),
    val bookmarkedJobIds: Set<Int> = emptySet(),
    val showBookmarksOnly: Boolean = false,
    val isLoading: Boolean = false,
    val isDarkTheme: Boolean = true,
    val showAlertsDialog: Boolean = false,
    val currentTab: NavTab = NavTab.HOME,
    val isOffline: Boolean = false,
    val syncMessage: String? = null,
    val hasLoadedJobs: Boolean = false,
    val cacheError: String? = null,
    val isFiltering: Boolean = false
)

class MainViewModel(
    private val repository: JobRepository = JobRepositoryProvider.getRepository(JobPulseApp.instance),
    private val preferences: UserPreferences? = null,
    private val savedStateHandle: SavedStateHandle = SavedStateHandle(),
    private val filterDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppState(
        bookmarkedJobIds = preferences?.bookmarks().orEmpty(),
        isDarkTheme = preferences?.darkTheme() ?: true,
        currentTab = NavTab.entries.firstOrNull { it.name == savedStateHandle.get<String>("tab") } ?: NavTab.HOME,
        selectedCategory = JobCategory.entries.firstOrNull { it.name == savedStateHandle.get<String>("category") } ?: JobCategory.ALL,
        selectedState = savedStateHandle["state"] ?: "All States",
        searchQuery = savedStateHandle["query"] ?: "",
        showBookmarksOnly = savedStateHandle["saved_only"] ?: false
    ))
    val uiState = _uiState.asStateFlow()
    private var refreshJob: CoroutineJob? = null
    private var observationJob: CoroutineJob? = null
    private var filteringJob: CoroutineJob? = null

    init { observeJobs(); syncData(false) }

    private fun observeJobs() {
        if (observationJob?.isActive == true) return
        observationJob = viewModelScope.launch {
            repository.getJobsStream().catch { error ->
                if (error is CancellationException) throw error
                _uiState.update { it.copy(cacheError = "Unable to read saved notices. Try Refresh.") }
            }.collect { jobs ->
                _uiState.update { it.copy(allJobs = jobs, hasLoadedJobs = true, cacheError = null) }
                filterJobs()
            }
        }
    }

    private fun filterJobs() {
        filteringJob?.cancel()
        val input = _uiState.value
        _uiState.update { it.copy(isFiltering = true) }
        filteringJob = viewModelScope.launch {
            val matches = withContext(filterDispatcher) {
                val query = input.searchQuery.trim()
                input.allJobs.filter { job ->
                    ensureActive()
                    (input.selectedCategory == JobCategory.ALL || job.category == input.selectedCategory) &&
                        (input.selectedCategory != JobCategory.STATE || input.selectedState == "All States" || job.state == input.selectedState || job.location.contains(input.selectedState, true)) &&
                        (query.isBlank() || job.title.contains(query, true) || job.organization.contains(query, true) || job.level.contains(query, true) || job.location.contains(query, true)) &&
                        (!input.showBookmarksOnly || job.id in input.bookmarkedJobIds)
                }
            }
            _uiState.update { it.copy(jobs = matches, isFiltering = false) }
        }
    }

    private fun syncData(force: Boolean) {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = repository.refresh(force)
                result.exceptionOrNull()?.let { if (it is CancellationException) throw it }
                _uiState.update { state -> state.copy(
                    isOffline = result.isFailure,
                    syncMessage = if (result.isSuccess) null else "Unable to refresh. Check the source for changes."
                ) }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { _uiState.update { it.copy(isOffline = true, syncMessage = "Unable to refresh. Please try again.") } }
            finally { _uiState.update { it.copy(isLoading = false) } }
        }
    }

    fun onRefresh() { observeJobs(); syncData(true) }
    fun onSelectTab(tab: NavTab) {
        savedStateHandle["tab"] = tab.name
        _uiState.update { it.copy(currentTab = tab) }
    }
    fun onToggleAlerts(show: Boolean) { _uiState.update { it.copy(showAlertsDialog = show) } }
    fun onToggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
        preferences?.saveDarkTheme(_uiState.value.isDarkTheme)
    }
    fun onToggleBookmark(jobId: Int) {
        _uiState.update { state -> state.copy(bookmarkedJobIds = if (jobId in state.bookmarkedJobIds) state.bookmarkedJobIds - jobId else state.bookmarkedJobIds + jobId) }
        preferences?.saveBookmarks(_uiState.value.bookmarkedJobIds)
        filterJobs()
    }
    fun onToggleBookmarksView() {
        val enabled = !_uiState.value.showBookmarksOnly
        savedStateHandle["saved_only"] = enabled
        onSelectTab(NavTab.HOME)
        _uiState.update { it.copy(showBookmarksOnly = enabled) }
        filterJobs()
    }
    fun onSearchQueryChanged(query: String) {
        savedStateHandle["query"] = query
        _uiState.update { it.copy(searchQuery = query) }
        filterJobs()
    }
    fun onCategorySelected(category: JobCategory) {
        savedStateHandle["category"] = category.name
        val state = if (category == JobCategory.STATE) _uiState.value.selectedState else "All States"
        savedStateHandle["state"] = state
        _uiState.update { it.copy(selectedCategory = category, selectedState = state) }
        filterJobs()
    }
    fun onStateSelected(stateName: String) {
        savedStateHandle["state"] = stateName
        _uiState.update { it.copy(selectedState = stateName) }
        filterJobs()
    }
}
