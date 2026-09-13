package com.example

import com.example.model.Job
import com.example.model.RecruitmentStatus
import com.example.ui.components.getEffectiveStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class DeadlineBoundaryTest {
    @Test fun dateOnlyDeadline_staysOpenThroughClosingDay() {
        val now = Calendar.getInstance().apply { set(2026, Calendar.SEPTEMBER, 15, 23, 59, 0) }.timeInMillis
        val job = Job(1, "Example", applicationClosingDate = "2026-09-15")
        assertEquals(RecruitmentStatus.CLOSING_SOON, job.getEffectiveStatus(now))
    }
    @Test fun yesterday_isClosedEvenLessThan24HoursAgo() {
        val now = Calendar.getInstance().apply { set(2026, Calendar.SEPTEMBER, 16, 0, 1, 0) }.timeInMillis
        val job = Job(1, "Example", applicationClosingDate = "15 Sep 2026")
        assertEquals(RecruitmentStatus.APPLICATION_CLOSED, job.getEffectiveStatus(now))
    }
}
