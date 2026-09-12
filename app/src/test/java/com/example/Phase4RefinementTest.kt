package com.example

import androidx.compose.ui.graphics.Color
import com.example.model.*
import com.example.ui.components.getEffectiveStatus
import com.example.ui.theme.DarkThemeTokens
import com.example.ui.theme.OrgBrandingRegistry
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class Phase4RefinementTest {

    @Test
    fun cutoffType_and_recruitmentStatus_useCorrectTerminology() {
        assertEquals("Official Notification", CutoffType.OFFICIAL.label)
        assertEquals("Notification Published", RecruitmentStatus.PUBLISHED.displayName)
    }

    @Test
    fun orgBranding_defaultCentral_usesOfficialRecruitmentNotification() {
        val customJob = Job(
            id = 999,
            title = "Research Scientist",
            category = JobCategory.CENTRAL,
            organization = "Ministry of Electronics"
        )
        val branding = OrgBrandingRegistry.forJob(customJob)
        assertEquals("Official Recruitment Notification", branding.authoritySubtext)
    }

    @Test
    fun effectiveStatus_derivesClosingSoon_whenWithin7Days() {
        val format = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 3)
        val dateStr = format.format(cal.time)

        val job = Job(
            id = 1,
            title = "Assistant Executive",
            applicationClosingDate = dateStr
        )
        assertEquals(RecruitmentStatus.CLOSING_SOON, job.getEffectiveStatus())
    }

    @Test
    fun effectiveStatus_derivesClosed_whenPastDate() {
        val format = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -5)
        val dateStr = format.format(cal.time)

        val job = Job(
            id = 2,
            title = "Section Officer",
            applicationClosingDate = dateStr
        )
        assertEquals(RecruitmentStatus.APPLICATION_CLOSED, job.getEffectiveStatus())
    }

    @Test
    fun effectiveStatus_derivesOpen_whenMoreThan7DaysAhead() {
        val format = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 25)
        val dateStr = format.format(cal.time)

        val job = Job(
            id = 3,
            title = "Probationary Officer",
            applicationClosingDate = dateStr
        )
        assertEquals(RecruitmentStatus.APPLICATION_OPEN, job.getEffectiveStatus())
    }

    @Test
    fun effectiveStatus_preservesExplicitStatus() {
        val job = Job(
            id = 4,
            title = "Sub Inspector",
            status = RecruitmentStatus.EXAM_SCHEDULED,
            applicationClosingDate = "01 Jan 2020" // even if past, explicit status takes precedence
        )
        assertEquals(RecruitmentStatus.EXAM_SCHEDULED, job.getEffectiveStatus())
    }

    @Test
    fun dummyJobs_cutoffSources_doNotUseMisleadingGazetted() {
        DummyJobs.forEach { job ->
            job.cutoffBenchmarks.forEach { cutoff ->
                assertFalse(
                    "Cutoff source for ${job.title} should not use Gazetted: ${cutoff.yearOrShift}",
                    cutoff.yearOrShift.contains("Gazetted", ignoreCase = true)
                )
            }
        }
    }

    @Test
    fun darkThemeTokens_refinedColors_matchDesignSystem() {
        assertEquals(Color(0xFF2F81F7), DarkThemeTokens.primary)
        assertEquals(Color(0xFFE53935), DarkThemeTokens.livePulse)
    }
}
