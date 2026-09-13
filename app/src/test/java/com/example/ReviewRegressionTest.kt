package com.example

import androidx.lifecycle.SavedStateHandle
import com.example.model.NavTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewRegressionTest {
    @Test fun savedFiltersAndTab_restoreWithoutPretendingCacheHasLoaded() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val state = SavedStateHandle(mapOf("tab" to "SEARCH", "category" to "BANK", "query" to "officer"))
            val model = MainViewModel(MainViewModelTest.FakeJobRepository(), savedStateHandle = state, filterDispatcher = dispatcher)
            assertEquals(NavTab.SEARCH, model.uiState.value.currentTab)
            assertEquals(JobCategory.BANK, model.uiState.value.selectedCategory)
            assertFalse(model.uiState.value.hasLoadedJobs)
            advanceUntilIdle()
            assertTrue(model.uiState.value.hasLoadedJobs)
            model.onSelectTab(NavTab.FEED)
            assertEquals("FEED", state.get<String>("tab"))
        } finally { Dispatchers.resetMain() }
    }

    @Test fun refresh_recoversFailedObservationAndKeepsErrorsSeparate() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val base = MainViewModelTest.FakeJobRepository()
            var attempts = 0
            val repo = object : com.example.data.repository.JobRepository by base {
                override fun getJobsStream() = flow {
                    if (attempts++ == 0) throw IOException("Cache read failed")
                    emit(listOf(Job(9, "Recovered notice")))
                }
            }
            val model = MainViewModel(repo, filterDispatcher = dispatcher)
            advanceUntilIdle()
            assertNotNull(model.uiState.value.cacheError)
            assertFalse(model.uiState.value.hasLoadedJobs)
            model.onRefresh()
            advanceUntilIdle()
            assertNull(model.uiState.value.cacheError)
            assertEquals(9, model.uiState.value.jobs.single().id)
        } finally { Dispatchers.resetMain() }
    }
}
