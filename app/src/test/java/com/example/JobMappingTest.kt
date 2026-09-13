package com.example

import com.example.data.remote.JobDto
import com.example.data.remote.OrganisationDto
import com.example.data.repository.JobMappers
import com.example.model.JobCategory
import com.example.model.RecruitmentStatus
import org.junit.Assert.*
import org.junit.Test

class JobMappingTest {
    @Test fun missingInformation_doesNotBecomePlausibleFacts() {
        val job = JobMappers.toDomain(JobMappers.toEntity(JobDto(id = 1, title = "Unreviewed notice")))
        assertFalse(job.isOfficialSource)
        assertEquals(RecruitmentStatus.DRAFT, job.status)
        assertEquals(JobCategory.ALL, job.category)
        assertEquals("Not announced", job.minQualification)
        assertEquals("Not announced", job.applicationFee)
        assertEquals("Not announced", job.ageLimit)
        assertEquals("Not announced", job.applicationClosingDate)
        assertEquals("Not announced", job.selectionStagesSummary)
        assertEquals("Not announced", job.quota)
        assertNull(job.lastVerifiedAt)
        assertTrue(job.noticeUrl.isEmpty())
    }
    @Test fun explicitInformation_isPreservedAcrossCacheMapping() {
        val dto = JobDto(
            id = 501, slug = "sbi-po-2026", title = "Probationary Officer", organisationId = "sbi",
            organization = "State Bank of India", category = "BANK", vacancies = 2000,
            educationalQualification = "Graduation in any discipline", minAge = 21, maxAge = 30,
            ageRelaxation = "See category rules", payScale = "As stated in source",
            applicationStartDate = "2026-09-01", applicationEndDate = "2026-09-21",
            selectionProcess = listOf("Prelims", "Mains", "Interview & GD"), syllabus = listOf("Reasoning", "English"),
            reservationInformation = "See vacancy table", cutoffInformation = "Historical source required",
            applicationUrl = "https://sbi.co.in/careers", officialNotificationUrl = "https://sbi.co.in/notice.pdf",
            sourceName = "State Bank of India", isOfficialSource = true, status = "APPLICATION_OPEN",
            lastVerifiedAt = "2026-09-01T09:00:00Z"
        )
        val job = JobMappers.toDomain(JobMappers.toEntity(dto))
        assertEquals(dto.id, job.id)
        assertEquals(dto.title, job.title)
        assertEquals(dto.organisationId, job.organisationId)
        assertEquals(2000, job.seats)
        assertEquals(JobCategory.BANK, job.category)
        assertEquals(dto.educationalQualification, job.minQualification)
        assertEquals(dto.applicationUrl, job.applyUrl)
        assertEquals(dto.applicationEndDate, job.applicationClosingDate)
        assertEquals(dto.ageRelaxation, job.ageRelaxation)
        assertEquals(dto.selectionProcess, job.selectionProcess)
        assertEquals("Prelims ➔ Mains ➔ Interview & GD", job.selectionStagesSummary)
        assertEquals(dto.syllabus, job.syllabusTopics)
        assertEquals(dto.reservationInformation, job.seatsAndReservation)
        assertEquals(dto.cutoffInformation, job.cutOffInfo)
        assertEquals(dto.lastVerifiedAt, job.lastVerifiedAt)
        assertTrue(job.isOfficialSource)
    }
    @Test fun categoryAndStatusMappings_areConservative() {
        assertEquals(JobCategory.RAILWAY, JobMappers.mapCategory("RRB"))
        assertEquals(JobCategory.BANK, JobMappers.mapCategory("IBPS"))
        assertEquals(JobCategory.DEFENSE, JobMappers.mapCategory("defence"))
        assertEquals(JobCategory.STATE, JobMappers.mapCategory("police"))
        assertEquals(JobCategory.ALL, JobMappers.mapCategory(null))
        assertEquals(JobCategory.ALL, JobMappers.mapCategory("unknown"))
        assertEquals(RecruitmentStatus.DRAFT, JobMappers.mapStatus(null))
        assertEquals(RecruitmentStatus.DRAFT, JobMappers.mapStatus("unknown"))
        assertEquals(RecruitmentStatus.EXAM_SCHEDULED, JobMappers.mapStatus("EXAM_SCHEDULED"))
    }
    @Test fun organisationMapping_preservesSource() {
        val dto = OrganisationDto("upsc", "Union Public Service Commission", "UPSC", officialWebsite = "https://upsc.gov.in")
        val org = JobMappers.toDomain(JobMappers.toEntity(dto))
        assertEquals(dto.id, org.id)
        assertEquals(dto.name, org.name)
        assertEquals(dto.officialWebsite, org.officialWebsite)
    }
}
