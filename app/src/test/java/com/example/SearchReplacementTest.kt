package com.example

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SearchReplacementTest {
    @get:Rule val rule = createComposeRule()
    @Test fun equalReplacementList_doesNotHideSearchResults() {
        lateinit var replace: () -> Unit
        rule.setContent {
            var jobs by remember { mutableStateOf(listOf(Job(123, "Research assistant")), neverEqualPolicy()) }
            replace = { jobs = jobs.map { it.copy() } }
            MyApplicationTheme { SearchScreen(rememberLazyListState(), emptySet(), {}, {}, allJobs = jobs) }
        }
        rule.waitUntil(5_000) { rule.onAllNodesWithTag("job_card_123").fetchSemanticsNodes().isNotEmpty() }
        rule.runOnIdle { replace() }
        rule.onNodeWithTag("job_card_123").assertExists()
    }
}
