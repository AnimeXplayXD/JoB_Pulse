package com.example

import com.example.data.repository.JobRepository
import com.example.model.DummyJobs
import com.example.model.Job
import com.example.model.JobCategory
import com.example.model.Organisation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    class FakeJobRepository(
        private val initialJobs: List<Job> = DummyJobs,
        var shouldFailSync: Boolean = false
    ) : JobRepository {
        val jobsFlow = MutableStateFlow(initialJobs)
        var refreshCallCount = 0
        var lastForceParam: Boolean? = null

        override fun getJobsStream(): Flow<List<Job>> = jobsFlow
        override fun getJobByIdStream(id: Int): Flow<Job?> = flowOf(jobsFlow.value.find { it.id == id })
        override fun getOrganisationsStream(): Flow<List<Organisation>> = flowOf(emptyList())
        override fun searchJobsStream(query: String): Flow<List<Job>> = flowOf(jobsFlow.value)

        override suspend fun refresh(force: Boolean): Result<Unit> {
            refreshCallCount++
            lastForceParam = force
            return if (shouldFailSync) {
                Result.failure(IOException("Network unreachable"))
            } else {
                Result.success(Unit)
            }
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun mainViewModel_initializesAndObservesRepositoryStream() = runTest {
        val repository = FakeJobRepository()
        val viewModel = MainViewModel(repository = repository)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isOffline)
        assertEquals(DummyJobs.size, state.jobs.size)
        assertEquals(DummyJobs.size, state.allJobs.size)
    }

    @Test
    fun mainViewModel_filtersByCategory() = runTest {
        val repository = FakeJobRepository()
        val viewModel = MainViewModel(repository = repository)

        advanceUntilIdle()

        viewModel.onCategorySelected(JobCategory.RAILWAY)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(JobCategory.RAILWAY, state.selectedCategory)
        assertTrue(state.jobs.all { it.category == JobCategory.RAILWAY })
        assertTrue(state.jobs.isNotEmpty())
    }

    @Test
    fun mainViewModel_filtersBySearchQuery() = runTest {
        val repository = FakeJobRepository()
        val viewModel = MainViewModel(repository = repository)

        advanceUntilIdle()

        viewModel.onSearchQueryChanged("SBI")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("SBI", state.searchQuery)
        assertTrue(state.jobs.all { it.organization.contains("SBI", ignoreCase = true) || it.title.contains("SBI", ignoreCase = true) })
    }

    @Test
    fun mainViewModel_togglesBookmarks_andViewsSavedJobs() = runTest {
        val repository = FakeJobRepository()
        val viewModel = MainViewModel(repository = repository)

        advanceUntilIdle()

        val firstJobId = viewModel.uiState.value.jobs.first().id
        viewModel.onToggleBookmark(firstJobId)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.bookmarkedJobIds.contains(firstJobId))

        viewModel.onToggleBookmarksView()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showBookmarksOnly)
        assertEquals(1, viewModel.uiState.value.jobs.size)
        assertEquals(firstJobId, viewModel.uiState.value.jobs.first().id)
    }

    @Test
    fun mainViewModel_handlesSyncFailure_entersOfflineModeGracefully() = runTest {
        val repository = FakeJobRepository(shouldFailSync = true)
        val viewModel = MainViewModel(repository = repository)

        advanceUntilIdle()

        // Cache remains available despite sync failure
        assertTrue(viewModel.uiState.value.jobs.isNotEmpty())
        assertTrue(viewModel.uiState.value.isOffline)
        assertNotNull(viewModel.uiState.value.syncMessage)

        // Trigger manual pull-to-refresh
        viewModel.onRefresh()
        advanceUntilIdle()

        assertEquals(2, repository.refreshCallCount)
        assertEquals(true, repository.lastForceParam)
        assertTrue(viewModel.uiState.value.isOffline)
        // Data is still preserved!
        assertTrue(viewModel.uiState.value.jobs.isNotEmpty())
    }
}
