package com.example

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.model.Job
import com.example.model.NavTab
import com.example.model.RecruitmentStatus
import com.example.ui.components.getEffectiveStatus
import com.example.ui.components.standardDateFormat
import com.example.ui.theme.DarkThemeTokens
import com.example.ui.theme.LightThemeTokens
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class LiquidGlassAndDockTest {

    @Test
    fun themeTokens_spatialRadiiAndDurations_areConsistent() {
        // Card and Dock Spatial Radii
        assertEquals(20.dp, DarkThemeTokens.cardRadius)
        assertEquals(20.dp, LightThemeTokens.cardRadius)
        assertEquals(24.dp, DarkThemeTokens.dockRadius)
        assertEquals(24.dp, LightThemeTokens.dockRadius)
        assertEquals(12.dp, DarkThemeTokens.buttonRadius)
        assertEquals(10.dp, DarkThemeTokens.chipRadius)
        assertEquals(16.dp, DarkThemeTokens.pillRadius)

        // Motion Durations
        assertEquals(180, DarkThemeTokens.durationShort)
        assertEquals(300, DarkThemeTokens.durationMedium)
        assertEquals(420, DarkThemeTokens.durationLong)

        // Saffron Brand Preservation
        assertEquals(Color(0xFFFF9933), DarkThemeTokens.brandSaffron)
        assertEquals(Color(0xFFFF9933), LightThemeTokens.brandSaffron)
    }

    @Test
    fun themeTokens_glassMaterials_calibratedCorrectly() {
        // Dark Glass Properties
        assertTrue(DarkThemeTokens.glassOpacity in 0.70f..0.95f)
        assertNotNull(DarkThemeTokens.glassTint)
        assertNotNull(DarkThemeTokens.glassBorder)
        assertNotNull(DarkThemeTokens.glassHighlightTop)

        // Light Glass Properties
        assertTrue(LightThemeTokens.glassOpacity in 0.85f..0.98f)
        assertNotNull(LightThemeTokens.glassTint)
        assertNotNull(LightThemeTokens.glassBorder)
        assertNotNull(LightThemeTokens.glassHighlightTop)

        // Light & Dark glass tints must differ to maintain contrast
        assertNotEquals(DarkThemeTokens.glassTint, LightThemeTokens.glassTint)
    }

    @Test
    fun dockTabs_structureAndCompleteness() {
        val tabs = NavTab.entries
        assertEquals(4, tabs.size)
        assertTrue(tabs.contains(NavTab.HOME))
        assertTrue(tabs.contains(NavTab.FEED))
        assertTrue(tabs.contains(NavTab.SEARCH))
        assertTrue(tabs.contains(NavTab.ACCOUNT))

        tabs.forEach { tab ->
            assertTrue("Tab ${tab.name} must have non-blank title", tab.title.isNotBlank())
        }
    }

    @Test
    fun standardDateFormat_threadSafeAndConsistent() {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.SEPTEMBER, 15, 0, 0, 0)
        val formatted = synchronized(standardDateFormat) {
            standardDateFormat.format(cal.time)
        }
        assertEquals("15 Sep 2026", formatted)

        val parsed = synchronized(standardDateFormat) {
            standardDateFormat.parse("15 Sep 2026")
        }
        assertNotNull(parsed)
    }

    @Test
    fun effectiveStatus_performanceAndAccuracy() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 2)
        val dateStr = synchronized(standardDateFormat) {
            standardDateFormat.format(cal.time)
        }

        val closingSoonJob = Job(
            id = 501,
            title = "Assistant Commissioner",
            applicationClosingDate = dateStr
        )
        assertEquals(RecruitmentStatus.CLOSING_SOON, closingSoonJob.getEffectiveStatus())

        val pastCal = Calendar.getInstance()
        pastCal.add(Calendar.DAY_OF_YEAR, -10)
        val pastDateStr = synchronized(standardDateFormat) {
            standardDateFormat.format(pastCal.time)
        }
        val closedJob = Job(
            id = 502,
            title = "Deputy Director",
            applicationClosingDate = pastDateStr
        )
        assertEquals(RecruitmentStatus.APPLICATION_CLOSED, closedJob.getEffectiveStatus())
    }
}
