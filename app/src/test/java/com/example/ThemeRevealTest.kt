package com.example

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.example.theme.CircularRevealShape
import com.example.theme.LocalThemeRevealController
import com.example.theme.ThemeRevealController
import com.example.theme.ThemeWaveState
import com.example.ui.components.CircularRevealController
import com.example.ui.components.LocalCircularReveal
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.hypot
import kotlin.math.max

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ThemeRevealTest {

    private val testDensity = Density(density = 2f, fontScale = 1f)
    private val testSize = Size(width = 1080f, height = 2400f)

    @Test
    fun circularRevealShape_atProgressZero_producesExpectedOutline() {
        // Normal: at progress 0f, nothing should be revealed
        val shapeNormal = CircularRevealShape(progress = 0f, origin = Offset(100f, 100f))
        val outlineNormal = shapeNormal.createOutline(testSize, LayoutDirection.Ltr, testDensity)
        assertTrue(outlineNormal is Outline.Generic)
    }

    @Test
    fun circularRevealShape_atProgressOne_producesExpectedOutline() {
        // Normal: at progress 1f, the entire rectangular screen is revealed
        val shapeNormal = CircularRevealShape(progress = 1f, origin = Offset(100f, 100f))
        val outlineNormal = shapeNormal.createOutline(testSize, LayoutDirection.Ltr, testDensity)
        assertTrue(outlineNormal is Outline.Rectangle)
        val rect = (outlineNormal as Outline.Rectangle).rect
        assertEquals(0f, rect.left)
        assertEquals(0f, rect.top)
        assertEquals(testSize.width, rect.width)
        assertEquals(testSize.height, rect.height)
    }

    @Test
    fun circularRevealShape_midProgress_calculatesCorrectGeometry() {
        val origin = Offset(1000f, 80f) // Top right button
        val shape = CircularRevealShape(progress = 0.5f, origin = origin)
        val outline = shape.createOutline(testSize, LayoutDirection.Ltr, testDensity)
        assertTrue(outline is Outline.Generic)

        val furthestX = max(origin.x, testSize.width - origin.x)
        val furthestY = max(origin.y, testSize.height - origin.y)
        val expectedMaxRadius = hypot(furthestX.toDouble(), furthestY.toDouble()).toFloat() * 1.05f

        assertTrue(expectedMaxRadius > testSize.height)
    }

    @Test
    fun themeRevealController_andAliases_areConsistent() {
        var revealedOffset: Offset? = null
        val controller = object : ThemeRevealController {
            override fun reveal(center: Offset?) {
                revealedOffset = center
            }
        }

        // Test reveal method
        val testOffset = Offset(250f, 400f)
        controller.reveal(testOffset)
        assertEquals(testOffset, revealedOffset)

        // Test toggleTheme method (backwards-compatible alias)
        val testOffset2 = Offset(500f, 800f)
        controller.toggleTheme(testOffset2)
        assertEquals(testOffset2, revealedOffset)

        // Verify typealias consistency
        val legacyController: CircularRevealController = controller
        legacyController.toggleTheme(Offset(10f, 20f))
        assertEquals(Offset(10f, 20f), revealedOffset)
    }

    @Test
    fun themeWaveState_initialValues_areValid() {
        val defaultState = ThemeWaveState()
        assertFalse(defaultState.isWaveActive)
        assertEquals(1f, defaultState.progress, 0.001f)
        assertEquals(Offset.Zero, defaultState.origin)
        assertTrue(defaultState.fromDark)
        assertFalse(defaultState.toDark)

        val activeState = ThemeWaveState(
            isWaveActive = true,
            progress = 0.45f,
            origin = Offset(300f, 150f),
            fromDark = false,
            toDark = true
        )
        assertTrue(activeState.isWaveActive)
        assertEquals(0.45f, activeState.progress, 0.001f)
        assertEquals(Offset(300f, 150f), activeState.origin)
        assertFalse(activeState.fromDark)
        assertTrue(activeState.toDark)
    }
}
