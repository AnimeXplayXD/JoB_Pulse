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
import java.util.Locale

object JobMappers {
    private val moshi = Moshi.Builder().build()
    private val listAdapter = moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))
    private fun known(value: String?): String = value?.takeIf { it.isNotBlank() } ?: "Not announced"

    fun mapCategory(categoryStr: String?): JobCategory {
        val value = categoryStr?.uppercase(Locale.ROOT) ?: return JobCategory.ALL
        return JobCategory.entries.firstOrNull { it.name == value } ?: when {
            value.contains("RAIL") || value == "RRB" -> JobCategory.RAILWAY
            value.contains("BANK") || value in setOf("IBPS", "SBI") -> JobCategory.BANK
            value.contains("DEFEN") || value == "MOD" -> JobCategory.DEFENSE
            value.contains("STATE") || value.contains("POLICE") || value.contains("TEACHING") || value in setOf("BPSC", "MPSC") -> JobCategory.STATE
            else -> JobCategory.ALL
        }
    }

    fun mapStatus(statusStr: String?): RecruitmentStatus = RecruitmentStatus.entries.firstOrNull {
        it.name == statusStr?.uppercase(Locale.ROOT)
    } ?: RecruitmentStatus.DRAFT

    fun toEntity(dto: JobDto): JobEntity = JobEntity(
        id = dto.id, slug = dto.slug, title = dto.title, organisationId = dto.organisationId,
        organization = dto.organization?.takeIf { it.isNotBlank() } ?: "Organization not provided",
        category = mapCategory(dto.category).name,
        shortDescription = dto.shortDescription, description = dto.description,
        employmentType = dto.employmentType, department = dto.department,
        vacancies = dto.vacancies?.takeIf { it >= 0 }, educationalQualification = dto.educationalQualification,
        minAge = dto.minAge, maxAge = dto.maxAge, ageRelaxation = dto.ageRelaxation,
        payScale = dto.payScale, applicationStartDate = dto.applicationStartDate,
        applicationEndDate = dto.applicationEndDate, examDate = dto.examDate,
        admitCardDate = dto.admitCardDate, resultDate = dto.resultDate,
        examMode = dto.examMode, examLocation = dto.examLocation, postingLocation = dto.postingLocation,
        selectionProcessJson = dto.selectionProcess?.let { listAdapter.toJson(it) },
        syllabusJson = dto.syllabus?.let { listAdapter.toJson(it) },
        reservationInformation = dto.reservationInformation, cutoffInformation = dto.cutoffInformation,
        applicationUrl = dto.applicationUrl, officialNotificationUrl = dto.officialNotificationUrl,
        officialSourceUrl = dto.officialSourceUrl, sourceName = dto.sourceName,
        isOfficialSource = dto.isOfficialSource ?: false, status = mapStatus(dto.status).name,
        publishedAt = dto.publishedAt, lastUpdatedAt = dto.lastUpdatedAt,
        lastVerifiedAt = dto.lastVerifiedAt, isDeleted = dto.isDeleted ?: false
    )

    private fun readList(json: String?): List<String> = try {
        json?.let { listAdapter.fromJson(it) }.orEmpty()
    } catch (_: Exception) { emptyList() }

    fun toDomain(entity: JobEntity): Job {
        val selection = readList(entity.selectionProcessJson)
        val syllabus = readList(entity.syllabusJson)
        val qualification = known(entity.educationalQualification)
        val salary = known(entity.payScale)
        val location = known(entity.postingLocation)
        val vacancies = entity.vacancies ?: 0
        val age = when {
            entity.minAge != null && entity.maxAge != null -> "${entity.minAge}–${entity.maxAge} years"
            entity.minAge != null -> "Minimum ${entity.minAge} years; maximum not announced"
            entity.maxAge != null -> "Maximum ${entity.maxAge} years; minimum not announced"
            else -> "Not announced"
        }
        return Job(
            id = entity.id, title = entity.title, category = mapCategory(entity.category),
            organization = entity.organization.ifBlank { "Organization not provided" },
            level = known(entity.department), salary = salary, location = location, seats = vacancies,
            quota = known(entity.reservationInformation), applyUrl = entity.applicationUrl,
            noticeUrl = entity.officialNotificationUrl.orEmpty(), officialSiteUrl = entity.officialSourceUrl.orEmpty(),
            minQualification = qualification, jobOverview = entity.description ?: entity.shortDescription.orEmpty(),
            locationDetails = location, applicationStartDate = known(entity.applicationStartDate),
            applicationClosingDate = known(entity.applicationEndDate), examDate = known(entity.examDate),
            ageLimit = age, applicationFee = "Not announced",
            selectionStagesSummary = selection.takeIf { it.isNotEmpty() }?.joinToString(" ➔ ") ?: "Not announced",
            syllabusTopics = syllabus, seatsAndReservation = entity.reservationInformation.orEmpty(),
            cutOffInfo = entity.cutoffInformation.orEmpty(),
            organisationId = entity.organisationId, shortDescription = entity.shortDescription,
            description = entity.description, employmentType = entity.employmentType, department = entity.department,
            vacancies = vacancies, educationalQualification = qualification, eligibility = qualification,
            minAge = entity.minAge, maxAge = entity.maxAge, ageRelaxation = entity.ageRelaxation,
            payScale = salary, applicationEndDate = known(entity.applicationEndDate),
            admitCardDate = entity.admitCardDate, resultDate = entity.resultDate,
            examMode = entity.examMode, examLocation = entity.examLocation, postingLocation = location,
            selectionProcess = selection, syllabus = syllabus,
            reservationInformation = entity.reservationInformation, cutoffInformation = entity.cutoffInformation,
            applicationUrl = entity.applicationUrl, officialNotificationUrl = entity.officialNotificationUrl.orEmpty(),
            officialSourceUrl = entity.officialSourceUrl.orEmpty(), sourceName = entity.sourceName ?: "Source not provided",
            isOfficialSource = entity.isOfficialSource, status = mapStatus(entity.status),
            publishedAt = entity.publishedAt, lastUpdatedAt = entity.lastUpdatedAt, lastVerifiedAt = entity.lastVerifiedAt
        )
    }

    fun toEntity(dto: OrganisationDto): OrganisationEntity = OrganisationEntity(
        dto.id, dto.name, dto.shortName, dto.type, dto.officialWebsite, dto.officialRecruitmentUrl, dto.brandingId, dto.lastVerifiedAt
    )
    fun toDomain(entity: OrganisationEntity): Organisation = Organisation(
        entity.id, entity.name, entity.shortName, entity.type, entity.officialWebsite, entity.officialRecruitmentUrl, entity.brandingId, entity.lastVerifiedAt
    )
}
