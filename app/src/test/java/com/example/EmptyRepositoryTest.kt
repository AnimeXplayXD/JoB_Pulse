package com.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EmptyRepositoryTest {
    @Test fun emptyRepository_isNeverReplacedWithFixtures() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val model = MainViewModel(MainViewModelTest.FakeJobRepository(initialJobs = emptyList()))
            advanceUntilIdle()
            assertTrue(model.uiState.value.allJobs.isEmpty())
            model.onSearchQueryChanged("anything")
            assertTrue(model.uiState.value.jobs.isEmpty())
        } finally { Dispatchers.resetMain() }
    }
}
