package com.example.data.remote

import com.example.model.DummyJobs
import java.io.IOException

interface RemoteJobDataSource {
    suspend fun fetchJobsSync(updatedSince: String? = null): JobsSyncResponseDto
    suspend fun fetchJobById(id: Int): JobDto?
    suspend fun fetchOrganisations(): List<OrganisationDto>
}

class RetrofitRemoteJobDataSource(
    private val apiService: RecruitmentApiService
) : RemoteJobDataSource {

    override suspend fun fetchJobsSync(updatedSince: String?): JobsSyncResponseDto {
        val response = apiService.getJobs(updatedSince = updatedSince)
        if (response.isSuccessful) {
            return response.body() ?: JobsSyncResponseDto(
                jobs = emptyList(),
                serverTime = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).apply {
                    timeZone = java.util.TimeZone.getTimeZone("UTC")
                }.format(java.util.Date())
            )
        } else {
            throw IOException("Failed to fetch jobs: HTTP ${response.code()} ${response.message()}")
        }
    }

    override suspend fun fetchJobById(id: Int): JobDto? {
        val response = apiService.getJobById(id)
        if (response.isSuccessful) {
            return response.body()
        } else if (response.code() == 404) {
            return null
        } else {
            throw IOException("Failed to fetch job $id: HTTP ${response.code()} ${response.message()}")
        }
    }

    override suspend fun fetchOrganisations(): List<OrganisationDto> {
        val response = apiService.getOrganisations()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw IOException("Failed to fetch organisations: HTTP ${response.code()} ${response.message()}")
        }
    }
}

/**
 * Development & fallback data source providing seeded fixtures for local offline development.
 * All records are explicitly identified as non-production fixtures.
 */
class DevelopmentJobDataSource : RemoteJobDataSource {

    private val sampleOrganisations = listOf(
        OrganisationDto(
            id = "sbi",
            name = "State Bank of India",
            shortName = "SBI",
            type = "Public Sector Bank",
            officialWebsite = "https://sbi.co.in",
            officialRecruitmentUrl = "https://sbi.co.in/careers",
            brandingId = "sbi",
            lastVerifiedAt = "2026-03-01T10:00:00Z"
        ),
        OrganisationDto(
            id = "rrb",
            name = "Railway Recruitment Boards",
            shortName = "RRB",
            type = "Ministry of Railways",
            officialWebsite = "https://indianrailways.gov.in",
            officialRecruitmentUrl = "https://rrbcdg.gov.in",
            brandingId = "rrb",
            lastVerifiedAt = "2026-03-01T10:00:00Z"
        ),
        OrganisationDto(
            id = "ssc",
            name = "Staff Selection Commission",
            shortName = "SSC",
            type = "Central Government Commission",
            officialWebsite = "https://ssc.gov.in",
            officialRecruitmentUrl = "https://ssc.gov.in",
            brandingId = "ssc",
            lastVerifiedAt = "2026-03-01T10:00:00Z"
        ),
        OrganisationDto(
            id = "upsc",
            name = "Union Public Service Commission",
            shortName = "UPSC",
            type = "Constitutional Authority",
            officialWebsite = "https://upsc.gov.in",
            officialRecruitmentUrl = "https://upsconline.nic.in",
            brandingId = "upsc",
            lastVerifiedAt = "2026-03-01T10:00:00Z"
        ),
        OrganisationDto(
            id = "police",
            name = "State Police Recruitment Board",
            shortName = "POLICE",
            type = "State Police Department",
            officialWebsite = "https://uppbpb.gov.in",
            officialRecruitmentUrl = "https://uppbpb.gov.in",
            brandingId = "police",
            lastVerifiedAt = "2026-03-01T10:00:00Z"
        ),
        OrganisationDto(
            id = "defense",
            name = "Indian Armed Forces",
            shortName = "MOD",
            type = "Ministry of Defence",
            officialWebsite = "https://mod.gov.in",
            officialRecruitmentUrl = "https://joinindianarmy.nic.in",
            brandingId = "defense",
            lastVerifiedAt = "2026-03-01T10:00:00Z"
        ),
        OrganisationDto(
            id = "mpsc",
            name = "Maharashtra Public Service Commission",
            shortName = "MPSC",
            type = "State Commission",
            officialWebsite = "https://mpsc.gov.in",
            officialRecruitmentUrl = "https://mpsc.gov.in",
            brandingId = "mpsc",
            lastVerifiedAt = "2026-03-01T10:00:00Z"
        ),
        OrganisationDto(
            id = "bpsc",
            name = "Bihar Public Service Commission",
            shortName = "BPSC",
            type = "State Commission",
            officialWebsite = "https://bpsc.bih.nic.in",
            officialRecruitmentUrl = "https://bpsc.bih.nic.in",
            brandingId = "bpsc",
            lastVerifiedAt = "2026-03-01T10:00:00Z"
        ),
        OrganisationDto(
            id = "ibps",
            name = "Institute of Banking Personnel Selection",
            shortName = "IBPS",
            type = "Autonomous Banking Body",
            officialWebsite = "https://ibps.in",
            officialRecruitmentUrl = "https://ibps.in",
            brandingId = "ibps",
            lastVerifiedAt = "2026-03-01T10:00:00Z"
        )
    )

    override suspend fun fetchJobsSync(updatedSince: String?): JobsSyncResponseDto {
        val dtos = DummyJobs.map { job ->
            JobDto(
                id = job.id,
                slug = "job-${job.id}",
                title = job.title,
                organisationId = job.organisationId,
                organization = job.organization,
                category = job.category.name,
                shortDescription = job.shortDescription,
                description = job.description,
                employmentType = job.employmentType,
                department = job.department,
                vacancies = job.vacancies,
                educationalQualification = job.educationalQualification,
                minAge = job.minAge,
                maxAge = job.maxAge,
                ageRelaxation = job.ageRelaxation,
                payScale = job.payScale,
                applicationStartDate = job.applicationStartDate,
                applicationEndDate = job.applicationEndDate,
                examDate = job.examDate,
                admitCardDate = job.admitCardDate,
                resultDate = job.resultDate,
                examMode = job.examMode,
                examLocation = job.examLocation,
                postingLocation = job.postingLocation,
                selectionProcess = job.selectionProcess,
                syllabus = job.syllabus,
                reservationInformation = job.reservationInformation,
                cutoffInformation = job.cutoffInformation,
                applicationUrl = job.applicationUrl,
                officialNotificationUrl = job.officialNotificationUrl,
                officialSourceUrl = job.officialSourceUrl,
                sourceName = job.sourceName,
                isOfficialSource = job.isOfficialSource,
                status = job.status.name,
                publishedAt = job.publishedAt,
                lastUpdatedAt = job.lastUpdatedAt,
                lastVerifiedAt = job.lastVerifiedAt,
                isDeleted = false,
                milestones = emptyList(),
                quotaCategories = emptyList(),
                examStages = emptyList(),
                cutoffs = emptyList(),
                documentsRequired = emptyList(),
                tags = listOf(job.category.name, job.organization)
            )
        }

        return JobsSyncResponseDto(
            jobs = dtos,
            deletedJobIds = emptyList(),
            serverTime = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }.format(java.util.Date()),
            organisations = sampleOrganisations
        )
    }

    override suspend fun fetchJobById(id: Int): JobDto? {
        val job = DummyJobs.find { it.id == id } ?: return null
        return JobDto(
            id = job.id,
            slug = "job-${job.id}",
            title = job.title,
            organisationId = job.organisationId,
            organization = job.organization,
            category = job.category.name,
            shortDescription = job.shortDescription,
            description = job.description,
            employmentType = job.employmentType,
            department = job.department,
            vacancies = job.vacancies,
            educationalQualification = job.educationalQualification,
            minAge = job.minAge,
            maxAge = job.maxAge,
            ageRelaxation = job.ageRelaxation,
            payScale = job.payScale,
            applicationStartDate = job.applicationStartDate,
            applicationEndDate = job.applicationEndDate,
            examDate = job.examDate,
            admitCardDate = job.admitCardDate,
            resultDate = job.resultDate,
            examMode = job.examMode,
            examLocation = job.examLocation,
            postingLocation = job.postingLocation,
            selectionProcess = job.selectionProcess,
            syllabus = job.syllabus,
            reservationInformation = job.reservationInformation,
            cutoffInformation = job.cutoffInformation,
            applicationUrl = job.applicationUrl,
            officialNotificationUrl = job.officialNotificationUrl,
            officialSourceUrl = job.officialSourceUrl,
            sourceName = job.sourceName,
            isOfficialSource = job.isOfficialSource,
            status = job.status.name,
            publishedAt = job.publishedAt,
            lastUpdatedAt = job.lastUpdatedAt,
            lastVerifiedAt = job.lastVerifiedAt,
            isDeleted = false,
            milestones = emptyList(),
            quotaCategories = emptyList(),
            examStages = emptyList(),
            cutoffs = emptyList(),
            documentsRequired = emptyList(),
            tags = listOf(job.category.name, job.organization)
        )
    }

    override suspend fun fetchOrganisations(): List<OrganisationDto> = sampleOrganisations
}
