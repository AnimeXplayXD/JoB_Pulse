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

import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Immutable
data class AppState(
    val selectedCategory: JobCategory = JobCategory.ALL,
    val selectedState: String = "All States",
    val searchQuery: String = "",
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

private data class FilterArgs(
    val query: String,
    val category: String,
    val state: String,
    val showBookmarksOnly: Boolean,
    val bookmarkedIds: Set<Int>
)

class MainViewModel(
    private val repository: JobRepository = JobRepositoryProvider.getRepository(JobPulseApp.instance),
    private val preferences: UserPreferences? = null,
    private val savedStateHandle: SavedStateHandle = SavedStateHandle(),
    private val filterDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppState(
        bookmarkedJobIds = emptySet(),
        isDarkTheme = true,
        currentTab = NavTab.entries.firstOrNull { it.name == savedStateHandle.get<String>("tab") } ?: NavTab.HOME,
        selectedCategory = JobCategory.entries.firstOrNull { it.name == savedStateHandle.get<String>("category") } ?: JobCategory.ALL,
        selectedState = savedStateHandle["state"] ?: "All States",
        searchQuery = savedStateHandle["query"] ?: "",
        showBookmarksOnly = savedStateHandle["saved_only"] ?: false
    ))
    val uiState = _uiState.asStateFlow()
    private var refreshJob: CoroutineJob? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedJobs = combine(
        _uiState.map { it.searchQuery }.distinctUntilChanged(),
        _uiState.map { it.selectedCategory }.distinctUntilChanged(),
        _uiState.map { it.selectedState }.distinctUntilChanged(),
        _uiState.map { it.showBookmarksOnly }.distinctUntilChanged(),
        _uiState.map { it.bookmarkedJobIds }.distinctUntilChanged()
    ) { query, category, state, showBookmarksOnly, bookmarkedIds ->
        FilterArgs(query, category.name, state, showBookmarksOnly, bookmarkedIds)
    }.flatMapLatest { args ->
        repository.getJobsPagingStream(
            args.query,
            args.category,
            args.state,
            args.showBookmarksOnly,
            args.bookmarkedIds
        )
    }.cachedIn(viewModelScope)

    init { 
        syncData(false)
        viewModelScope.launch {
            preferences?.bookmarks?.collect { bookmarks ->
                _uiState.update { it.copy(bookmarkedJobIds = bookmarks) }
            }
        }
        viewModelScope.launch {
            preferences?.darkTheme?.collect { isDark ->
                _uiState.update { it.copy(isDarkTheme = isDark) }
            }
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

    fun onRefresh() { syncData(true) }
    fun onSelectTab(tab: NavTab) {
        savedStateHandle["tab"] = tab.name
        _uiState.update { it.copy(currentTab = tab) }
    }
    fun onToggleAlerts(show: Boolean) { _uiState.update { it.copy(showAlertsDialog = show) } }
    fun onToggleTheme() {
        val newTheme = !_uiState.value.isDarkTheme
        _uiState.update { it.copy(isDarkTheme = newTheme) }
        viewModelScope.launch { preferences?.saveDarkTheme(newTheme) }
    }
    fun onToggleBookmark(jobId: Int) {
        val current = _uiState.value.bookmarkedJobIds
        val newBookmarks = if (jobId in current) current - jobId else current + jobId
        _uiState.update { state -> state.copy(bookmarkedJobIds = newBookmarks) }
        viewModelScope.launch { preferences?.saveBookmarks(newBookmarks) }
    }
    fun onToggleBookmarksView() {
        val enabled = !_uiState.value.showBookmarksOnly
        savedStateHandle["saved_only"] = enabled
        onSelectTab(NavTab.HOME)
        _uiState.update { it.copy(showBookmarksOnly = enabled) }
    }
    fun onSearchQueryChanged(query: String) {
        savedStateHandle["query"] = query
        _uiState.update { it.copy(searchQuery = query) }
    }
    fun onCategorySelected(category: JobCategory) {
        savedStateHandle["category"] = category.name
        val state = if (category == JobCategory.STATE) _uiState.value.selectedState else "All States"
        savedStateHandle["state"] = state
        _uiState.update { it.copy(selectedCategory = category, selectedState = state) }
    }
    fun onStateSelected(stateName: String) {
        savedStateHandle["state"] = stateName
        _uiState.update { it.copy(selectedState = stateName) }
    }
}
