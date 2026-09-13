package com.example

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.UserPreferences
import com.example.data.repository.JobRepository
import com.example.data.repository.JobRepositoryProvider
import com.example.model.NavTab
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job as CoroutineJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
    val syncMessage: String? = null
)

class MainViewModel(
    private val repository: JobRepository = JobRepositoryProvider.getRepository(JobPulseApp.instance),
    private val preferences: UserPreferences? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppState(
        bookmarkedJobIds = preferences?.bookmarks().orEmpty(),
        isDarkTheme = preferences?.darkTheme() ?: true
    ))
    val uiState = _uiState.asStateFlow()
    private var refreshJob: CoroutineJob? = null

    init {
        viewModelScope.launch {
            repository.getJobsStream().catch { error ->
                if (error is CancellationException) throw error
                _uiState.update { it.copy(isOffline = true, syncMessage = "Unable to read saved notices. Try again.") }
            }.collect { jobs ->
                _uiState.update { filtered(it.copy(allJobs = jobs)) }
            }
        }
        syncData(false)
    }

    private fun syncData(force: Boolean) {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = repository.refresh(force)
                result.exceptionOrNull()?.let { if (it is CancellationException) throw it }
                _uiState.update { state ->
                    state.copy(
                        isOffline = result.isFailure,
                        syncMessage = if (result.isSuccess) null else if (state.allJobs.isEmpty())
                            "Updates are unavailable. No cached notices are available." else
                            "Unable to refresh. Showing saved notices; check the source for changes."
                    )
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.update { it.copy(isOffline = true, syncMessage = "Unable to refresh. Please try again.") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onRefresh() = syncData(true)
    fun onSelectTab(tab: NavTab) { _uiState.update { it.copy(currentTab = tab) } }
    fun onToggleAlerts(show: Boolean) { _uiState.update { it.copy(showAlertsDialog = show) } }
    fun onToggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
        preferences?.saveDarkTheme(_uiState.value.isDarkTheme)
    }
    fun onToggleBookmark(jobId: Int) {
        _uiState.update { state ->
            val bookmarks = if (jobId in state.bookmarkedJobIds) state.bookmarkedJobIds - jobId else state.bookmarkedJobIds + jobId
            filtered(state.copy(bookmarkedJobIds = bookmarks))
        }
        preferences?.saveBookmarks(_uiState.value.bookmarkedJobIds)
    }
    fun onToggleBookmarksView() {
        _uiState.update { filtered(it.copy(showBookmarksOnly = !it.showBookmarksOnly, currentTab = NavTab.HOME)) }
    }
    fun onSearchQueryChanged(query: String) { _uiState.update { filtered(it.copy(searchQuery = query)) } }
    fun onCategorySelected(category: JobCategory) {
        _uiState.update { filtered(it.copy(selectedCategory = category, selectedState = if (category == JobCategory.STATE) it.selectedState else "All States")) }
    }
    fun onStateSelected(stateName: String) { _uiState.update { filtered(it.copy(selectedState = stateName)) } }

    private fun filtered(state: AppState): AppState = state.copy(jobs = state.allJobs.filter { job ->
        (state.selectedCategory == JobCategory.ALL || job.category == state.selectedCategory) &&
            (state.selectedCategory != JobCategory.STATE || state.selectedState == "All States" || job.state == state.selectedState || job.location.contains(state.selectedState, true)) &&
            (state.searchQuery.isBlank() || listOf(job.title, job.organization, job.level, job.location).any { it.contains(state.searchQuery.trim(), true) }) &&
            (!state.showBookmarksOnly || job.id in state.bookmarkedJobIds)
    })
}
