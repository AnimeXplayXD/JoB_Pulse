package com.example.data.repository

import com.example.data.local.JobEntity
import com.example.data.local.OrganisationEntity
import com.example.data.remote.JobDto
import com.example.data.remote.OrganisationDto
import com.example.model.Job
import com.example.model.JobCategory
import com.example.model.Organisation
import com.example.model.RecruitmentStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

object JobMappers {

    private val moshi = Moshi.Builder().build()
    private val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
    private val stringListAdapter = moshi.adapter<List<String>>(stringListType)

    fun mapCategory(categoryStr: String?): JobCategory {
        if (categoryStr == null) return JobCategory.CENTRAL
        return try {
            JobCategory.valueOf(categoryStr.uppercase())
        } catch (e: Exception) {
            when {
                categoryStr.contains("rail", ignoreCase = true) || categoryStr.equals("rrb", ignoreCase = true) -> JobCategory.RAILWAY
                categoryStr.contains("bank", ignoreCase = true) || categoryStr.equals("ibps", ignoreCase = true) || categoryStr.equals("sbi", ignoreCase = true) -> JobCategory.BANK
                categoryStr.contains("defen", ignoreCase = true) || categoryStr.contains("defence", ignoreCase = true) || categoryStr.equals("mod", ignoreCase = true) -> JobCategory.DEFENSE
                categoryStr.contains("state", ignoreCase = true) || categoryStr.contains("police", ignoreCase = true) || categoryStr.contains("teaching", ignoreCase = true) || categoryStr.equals("bpsc", ignoreCase = true) || categoryStr.equals("mpsc", ignoreCase = true) -> JobCategory.STATE
                else -> JobCategory.CENTRAL
            }
        }
    }

    fun mapStatus(statusStr: String?): RecruitmentStatus {
        if (statusStr == null) return RecruitmentStatus.APPLICATION_OPEN
        return try {
            RecruitmentStatus.valueOf(statusStr.uppercase())
        } catch (e: Exception) {
            RecruitmentStatus.APPLICATION_OPEN
        }
    }

    fun toEntity(dto: JobDto): JobEntity {
        return JobEntity(
            id = dto.id,
            slug = dto.slug,
            title = dto.title,
            organisationId = dto.organisationId,
            organization = dto.organization ?: dto.organisationId?.uppercase() ?: "Government of India",
            category = dto.category ?: JobCategory.CENTRAL.name,
            shortDescription = dto.shortDescription,
            description = dto.description,
            employmentType = dto.employmentType,
            department = dto.department,
            vacancies = dto.vacancies,
            educationalQualification = dto.educationalQualification,
            minAge = dto.minAge,
            maxAge = dto.maxAge,
            ageRelaxation = dto.ageRelaxation,
            payScale = dto.payScale,
            applicationStartDate = dto.applicationStartDate,
            applicationEndDate = dto.applicationEndDate,
            examDate = dto.examDate,
            admitCardDate = dto.admitCardDate,
            resultDate = dto.resultDate,
            examMode = dto.examMode,
            examLocation = dto.examLocation,
            postingLocation = dto.postingLocation,
            selectionProcessJson = dto.selectionProcess?.let { stringListAdapter.toJson(it) },
            syllabusJson = dto.syllabus?.let { stringListAdapter.toJson(it) },
            reservationInformation = dto.reservationInformation,
            cutoffInformation = dto.cutoffInformation,
            applicationUrl = dto.applicationUrl,
            officialNotificationUrl = dto.officialNotificationUrl,
            officialSourceUrl = dto.officialSourceUrl,
            sourceName = dto.sourceName,
            isOfficialSource = dto.isOfficialSource ?: true,
            status = dto.status ?: RecruitmentStatus.APPLICATION_OPEN.name,
            publishedAt = dto.publishedAt,
            lastUpdatedAt = dto.lastUpdatedAt,
            lastVerifiedAt = dto.lastVerifiedAt,
            isDeleted = dto.isDeleted ?: false
        )
    }

    fun toDomain(entity: JobEntity): Job {
        val selectionProcessList = entity.selectionProcessJson?.let {
            try { stringListAdapter.fromJson(it) } catch (e: Exception) { null }
        } ?: emptyList()

        val syllabusList = entity.syllabusJson?.let {
            try { stringListAdapter.fromJson(it) } catch (e: Exception) { null }
        } ?: emptyList()

        val vacanciesCount = entity.vacancies ?: 0
        val orgName = entity.organization.ifBlank { "Government of India" }
        val qualification = entity.educationalQualification ?: "Graduate"
        val salaryStr = entity.payScale ?: "Pay Scale As Per Rules"
        val locationStr = entity.postingLocation ?: "All India"
        val noticeStr = entity.officialNotificationUrl ?: ""
        val officialSiteStr = entity.officialSourceUrl ?: ""

        return Job(
            id = entity.id,
            title = entity.title,
            category = mapCategory(entity.category),
            organization = orgName,
            level = entity.department ?: "Central / State Cadre",
            salary = salaryStr,
            location = locationStr,
            seats = vacanciesCount,
            quota = entity.reservationInformation ?: "UR / OBC / SC / ST / EWS",
            applyUrl = entity.applicationUrl,
            noticeUrl = noticeStr,
            officialSiteUrl = officialSiteStr,
            minQualification = qualification,
            jobOverview = entity.shortDescription ?: entity.description ?: "",
            locationDetails = locationStr,
            applicationStartDate = entity.applicationStartDate ?: "Not announced",
            applicationClosingDate = entity.applicationEndDate ?: "Not announced",
            examDate = entity.examDate ?: "Not announced",
            selectionStagesSummary = if (selectionProcessList.isNotEmpty()) selectionProcessList.joinToString(" ➔ ") else "Written Screening ➔ Document Verification",
            syllabusTopics = syllabusList,
            organisationId = entity.organisationId,
            shortDescription = entity.shortDescription,
            description = entity.description,
            employmentType = entity.employmentType,
            department = entity.department,
            vacancies = vacanciesCount,
            educationalQualification = qualification,
            minAge = entity.minAge,
            maxAge = entity.maxAge,
            ageRelaxation = entity.ageRelaxation,
            payScale = salaryStr,
            applicationEndDate = entity.applicationEndDate ?: "Not announced",
            admitCardDate = entity.admitCardDate,
            resultDate = entity.resultDate,
            examMode = entity.examMode,
            examLocation = entity.examLocation,
            postingLocation = locationStr,
            selectionProcess = selectionProcessList,
            syllabus = syllabusList,
            reservationInformation = entity.reservationInformation,
            cutoffInformation = entity.cutoffInformation,
            applicationUrl = entity.applicationUrl,
            officialNotificationUrl = noticeStr,
            officialSourceUrl = officialSiteStr,
            sourceName = entity.sourceName ?: orgName,
            isOfficialSource = entity.isOfficialSource,
            status = mapStatus(entity.status),
            publishedAt = entity.publishedAt,
            lastUpdatedAt = entity.lastUpdatedAt,
            lastVerifiedAt = entity.lastVerifiedAt
        )
    }

    fun toEntity(dto: OrganisationDto): OrganisationEntity {
        return OrganisationEntity(
            id = dto.id,
            name = dto.name,
            shortName = dto.shortName,
            type = dto.type,
            officialWebsite = dto.officialWebsite,
            officialRecruitmentUrl = dto.officialRecruitmentUrl,
            brandingId = dto.brandingId,
            lastVerifiedAt = dto.lastVerifiedAt
        )
    }

    fun toDomain(entity: OrganisationEntity): Organisation {
        return Organisation(
            id = entity.id,
            name = entity.name,
            shortName = entity.shortName,
            type = entity.type,
            officialWebsite = entity.officialWebsite,
            officialRecruitmentUrl = entity.officialRecruitmentUrl,
            brandingId = entity.brandingId,
            lastVerifiedAt = entity.lastVerifiedAt
        )
    }
}
