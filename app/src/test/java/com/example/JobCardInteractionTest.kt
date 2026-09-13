package com.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import com.example.model.Job
import com.example.ui.components.JobCardItem
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
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
class JobCardInteractionTest {
    @get:Rule val rule = createComposeRule()
    private val job = Job(id = 7, title = "Research assistant")

    @Test fun previewAndDetails_areSeparateActions() {
        var opens = 0
        rule.setContent {
            MyApplicationTheme {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    JobCardItem(job, false, {}, onOpenDetails = { opens++ })
                }
            }
        }
        rule.onNodeWithTag("job_card_7").performClick()
        rule.runOnIdle { assertEquals(0, opens) }
        rule.onNodeWithTag("open_job_7").performScrollTo().performClick()
        rule.runOnIdle { assertEquals(1, opens) }
    }

    @Test fun bookmark_doesNotExpandOrNavigate() {
        var saves = 0
        var opens = 0
        rule.setContent { MyApplicationTheme { JobCardItem(job, false, { saves++ }, onOpenDetails = { opens++ }) } }
        rule.onNodeWithTag("bookmark_7").performClick()
        rule.onNodeWithTag("open_job_7").assertDoesNotExist()
        rule.runOnIdle { assertEquals(1, saves); assertEquals(0, opens) }
    }

    @Test fun expandedPreview_survivesRestoration() {
        val restoration = StateRestorationTester(rule)
        restoration.setContent { MyApplicationTheme { JobCardItem(job, false, {}) } }
        rule.onNodeWithTag("job_card_7").performClick()
        restoration.emulateSavedInstanceStateRestore()
        rule.onNodeWithTag("open_job_7").assertExists()
    }
}
