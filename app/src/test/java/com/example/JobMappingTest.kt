package com.example

import com.example.data.local.JobEntity
import com.example.data.remote.JobDto
import com.example.data.remote.OrganisationDto
import com.example.data.repository.JobMappers
import com.example.model.JobCategory
import com.example.model.RecruitmentStatus
import org.junit.Assert.*
import org.junit.Test

class JobMappingTest {

    @Test
    fun jobDto_toEntity_andToDomain_preservesAllFields() {
        val dto = JobDto(
            id = 501,
            slug = "sbi-po-2026",
            title = "Probationary Officer 2026",
            organisationId = "sbi",
            organization = "State Bank of India",
            category = "BANK",
            shortDescription = "Recruitment of Probationary Officers across India",
            description = "Detailed notice for 2000 PO vacancies.",
            employmentType = "Regular Probationary",
            department = "Junior Management Grade Scale I",
            vacancies = 2000,
            educationalQualification = "Graduation in any discipline",
            minAge = 21,
            maxAge = 30,
            ageRelaxation = "SC/ST: 5 years, OBC: 3 years",
            payScale = "Basic ₹41,960 + 4 increments",
            applicationStartDate = "2026-09-01",
            applicationEndDate = "2026-09-21",
            examDate = "2026-11-15",
            admitCardDate = "2026-11-01",
            resultDate = "2026-12-20",
            examMode = "Online Computer Based Test",
            examLocation = "Nationwide centres",
            postingLocation = "Anywhere in India",
            selectionProcess = listOf("Prelims", "Mains", "Interview & GD"),
            syllabus = listOf("Quantitative Aptitude", "Reasoning", "English Language", "Banking Awareness"),
            reservationInformation = "SC: 300, ST: 150, OBC: 540, EWS: 200, GEN: 810",
            cutoffInformation = "Prelims Gen: 58.50, OBC: 58.50",
            applicationUrl = "https://sbi.co.in/careers/po-apply",
            officialNotificationUrl = "https://sbi.co.in/po-2026.pdf",
            officialSourceUrl = "https://sbi.co.in/careers",
            sourceName = "State Bank of India",
            isOfficialSource = true,
            status = "APPLICATION_OPEN",
            publishedAt = "2026-08-30T10:00:00Z",
            lastUpdatedAt = "2026-09-01T08:00:00Z",
            lastVerifiedAt = "2026-09-01T09:00:00Z",
            isDeleted = false
        )

        // Transform DTO -> Entity
        val entity = JobMappers.toEntity(dto)
        assertEquals(501, entity.id)
        assertEquals("Probationary Officer 2026", entity.title)
        assertEquals("sbi", entity.organisationId)
        assertEquals(2000, entity.vacancies)
        assertEquals("BANK", entity.category)
        assertEquals("2026-09-01", entity.applicationStartDate)
        assertFalse(entity.isDeleted)

        // Transform Entity -> Domain
        val domain = JobMappers.toDomain(entity)
        assertEquals(501, domain.id)
        assertEquals("Probationary Officer 2026", domain.title)
        assertEquals(JobCategory.BANK, domain.category)
        assertEquals("State Bank of India", domain.organization)
        assertEquals(2000, domain.vacancies)
        assertEquals(2000, domain.seats) // backward compatibility
        assertEquals("Graduation in any discipline", domain.minQualification)
        assertEquals("https://sbi.co.in/careers/po-apply", domain.applyUrl)
        assertEquals(RecruitmentStatus.APPLICATION_OPEN, domain.status)
        assertEquals(3, domain.selectionProcess.size)
        assertEquals("Prelims ➔ Mains ➔ Interview & GD", domain.selectionStagesSummary)
        assertEquals(4, domain.syllabus.size)
    }

    @Test
    fun mapCategory_handlesKnownAndUnknownVariantsGracefully() {
        assertEquals(JobCategory.RAILWAY, JobMappers.mapCategory("railway"))
        assertEquals(JobCategory.RAILWAY, JobMappers.mapCategory("RRB"))
        assertEquals(JobCategory.BANK, JobMappers.mapCategory("bank"))
        assertEquals(JobCategory.BANK, JobMappers.mapCategory("IBPS"))
        assertEquals(JobCategory.DEFENSE, JobMappers.mapCategory("defense"))
        assertEquals(JobCategory.DEFENSE, JobMappers.mapCategory("defence"))
        assertEquals(JobCategory.STATE, JobMappers.mapCategory("police"))
        assertEquals(JobCategory.CENTRAL, JobMappers.mapCategory("unknown_category"))
        assertEquals(JobCategory.CENTRAL, JobMappers.mapCategory(null))
    }

    @Test
    fun mapStatus_handlesAllStatusStrings() {
        assertEquals(RecruitmentStatus.APPLICATION_OPEN, JobMappers.mapStatus("APPLICATION_OPEN"))
        assertEquals(RecruitmentStatus.EXAM_SCHEDULED, JobMappers.mapStatus("EXAM_SCHEDULED"))
        assertEquals(RecruitmentStatus.RESULT_RELEASED, JobMappers.mapStatus("RESULT_RELEASED"))
        assertEquals(RecruitmentStatus.APPLICATION_OPEN, JobMappers.mapStatus("INVALID_STATUS"))
        assertEquals(RecruitmentStatus.APPLICATION_OPEN, JobMappers.mapStatus(null))
    }

    @Test
    fun organisation_mapping_isAccurate() {
        val dto = OrganisationDto(
            id = "upsc",
            name = "Union Public Service Commission",
            shortName = "UPSC",
            type = "Constitutional Body",
            officialWebsite = "https://upsc.gov.in",
            officialRecruitmentUrl = "https://upsconline.nic.in",
            brandingId = "upsc",
            lastVerifiedAt = "2026-03-01T00:00:00Z"
        )

        val entity = JobMappers.toEntity(dto)
        val domain = JobMappers.toDomain(entity)

        assertEquals("upsc", domain.id)
        assertEquals("Union Public Service Commission", domain.name)
        assertEquals("UPSC", domain.shortName)
        assertEquals("https://upsc.gov.in", domain.officialWebsite)
    }
}
