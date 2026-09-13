package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.model.NavTab
import com.example.ui.components.GlassyDock
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w800dp-h1000dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DockLayoutTest {
    @get:Rule val rule = createComposeRule()

    @Test fun wideWindow_keepsCapsuleCompactAcrossDestinations() {
        rule.setContent {
            MyApplicationTheme {
                var tab by remember { mutableStateOf(NavTab.HOME) }
                Box(Modifier.width(600.dp)) { GlassyDock(tab, { tab = it }, true, true) }
            }
        }
        rule.onNodeWithTag("floating_glassy_dock").assertWidthIsEqualTo(300.dp)
        NavTab.entries.forEach { tab ->
            rule.onNodeWithTag("dock_${tab.name}").performClick().assertIsSelected()
            rule.onNodeWithTag("floating_glassy_dock").assertWidthIsEqualTo(300.dp)
            rule.onNodeWithTag("dock_${tab.name}").assertWidthIsAtLeast(48.dp).assertHeightIsAtLeast(48.dp)
        }
    }

    @Test fun drag_selectsDestinationBeforeRelease() {
        rule.setContent {
            MyApplicationTheme {
                var tab by remember { mutableStateOf(NavTab.HOME) }
                Box(Modifier.width(400.dp)) { GlassyDock(tab, { tab = it }, true, true) }
            }
        }
        val dock = rule.onNodeWithTag("floating_glassy_dock")
        val origin = dock.fetchSemanticsNode().boundsInRoot.topLeft
        val start = rule.onNodeWithTag("dock_HOME").fetchSemanticsNode().boundsInRoot.center - origin
        val end = rule.onNodeWithTag("dock_FEED").fetchSemanticsNode().boundsInRoot.center - origin
        dock.performTouchInput { down(start); moveTo(end, durationMillis = 200) }
        rule.onNodeWithTag("dock_FEED").assertIsSelected()
        dock.performTouchInput { up() }
    }

    @Test fun narrowWindow_fitsWithoutSacrificingTouchTargets() {
        rule.setContent {
            MyApplicationTheme {
                Box(Modifier.width(260.dp)) { GlassyDock(NavTab.HOME, {}, true, true) }
            }
        }
        rule.onNodeWithTag("floating_glassy_dock").assertWidthIsEqualTo(228.dp)
        NavTab.entries.forEach {
            rule.onNodeWithTag("dock_${it.name}").assertWidthIsAtLeast(48.dp).assertHeightIsAtLeast(48.dp)
        }
    }

    @Test fun largeText_expandsCapsuleWithinAvailableWidth() {
        rule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 3f)) {
                MyApplicationTheme {
                    Box(Modifier.width(400.dp)) { GlassyDock(NavTab.ACCOUNT, {}, true, true) }
                }
            }
        }
        rule.onNodeWithTag("floating_glassy_dock").assertWidthIsEqualTo(368.dp)
        rule.onNodeWithTag("dock_ACCOUNT").assertContentDescriptionEquals("Account").assertHeightIsAtLeast(48.dp)
    }
}
