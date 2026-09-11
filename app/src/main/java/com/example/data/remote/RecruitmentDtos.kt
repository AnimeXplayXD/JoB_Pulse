package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MilestoneDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "event_date") val eventDate: String? = null,
    @Json(name = "is_completed") val isCompleted: Boolean = false,
    @Json(name = "notification_url") val notificationUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class QuotaCategoryDto(
    @Json(name = "category_name") val categoryName: String,
    @Json(name = "vacancy_count") val vacancyCount: Int,
    @Json(name = "reservation_percentage") val reservationPercentage: Double? = null,
    @Json(name = "notes") val notes: String? = null
)

@JsonClass(generateAdapter = true)
data class ExamStageDto(
    @Json(name = "stage_number") val stageNumber: Int,
    @Json(name = "stage_name") val stageName: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "is_qualifying_only") val isQualifyingOnly: Boolean = false
)

@JsonClass(generateAdapter = true)
data class CutoffDto(
    @Json(name = "category") val category: String,
    @Json(name = "score") val score: Double,
    @Json(name = "stage_name") val stageName: String? = null,
    @Json(name = "exam_year") val examYear: Int? = null
)

@JsonClass(generateAdapter = true)
data class OrganisationDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "short_name") val shortName: String,
    @Json(name = "type") val type: String? = null,
    @Json(name = "official_website") val officialWebsite: String? = null,
    @Json(name = "official_recruitment_url") val officialRecruitmentUrl: String? = null,
    @Json(name = "branding_id") val brandingId: String? = null,
    @Json(name = "last_verified_at") val lastVerifiedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class JobDto(
    @Json(name = "id") val id: Int,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "title") val title: String,
    @Json(name = "organisation_id") val organisationId: String? = null,
    @Json(name = "organization") val organization: String? = null,
    @Json(name = "category") val category: String? = null,
    @Json(name = "short_description") val shortDescription: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "employment_type") val employmentType: String? = null,
    @Json(name = "department") val department: String? = null,
    @Json(name = "vacancies") val vacancies: Int? = null,
    @Json(name = "educational_qualification") val educationalQualification: String? = null,
    @Json(name = "min_age") val minAge: Int? = null,
    @Json(name = "max_age") val maxAge: Int? = null,
    @Json(name = "age_relaxation") val ageRelaxation: String? = null,
    @Json(name = "pay_scale") val payScale: String? = null,
    @Json(name = "application_start_date") val applicationStartDate: String? = null,
    @Json(name = "application_end_date") val applicationEndDate: String? = null,
    @Json(name = "exam_date") val examDate: String? = null,
    @Json(name = "admit_card_date") val admitCardDate: String? = null,
    @Json(name = "result_date") val resultDate: String? = null,
    @Json(name = "exam_mode") val examMode: String? = null,
    @Json(name = "exam_location") val examLocation: String? = null,
    @Json(name = "posting_location") val postingLocation: String? = null,
    @Json(name = "selection_process") val selectionProcess: List<String>? = null,
    @Json(name = "syllabus") val syllabus: List<String>? = null,
    @Json(name = "reservation_information") val reservationInformation: String? = null,
    @Json(name = "cutoff_information") val cutoffInformation: String? = null,
    @Json(name = "application_url") val applicationUrl: String? = null,
    @Json(name = "official_notification_url") val officialNotificationUrl: String? = null,
    @Json(name = "official_source_url") val officialSourceUrl: String? = null,
    @Json(name = "source_name") val sourceName: String? = null,
    @Json(name = "is_official_source") val isOfficialSource: Boolean? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "published_at") val publishedAt: String? = null,
    @Json(name = "last_updated_at") val lastUpdatedAt: String? = null,
    @Json(name = "last_verified_at") val lastVerifiedAt: String? = null,
    @Json(name = "is_deleted") val isDeleted: Boolean? = false,
    @Json(name = "milestones") val milestones: List<MilestoneDto>? = null,
    @Json(name = "quota_categories") val quotaCategories: List<QuotaCategoryDto>? = null,
    @Json(name = "exam_stages") val examStages: List<ExamStageDto>? = null,
    @Json(name = "cutoffs") val cutoffs: List<CutoffDto>? = null,
    @Json(name = "documents_required") val documentsRequired: List<String>? = null,
    @Json(name = "tags") val tags: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class JobsSyncResponseDto(
    @Json(name = "jobs") val jobs: List<JobDto>,
    @Json(name = "deleted_job_ids") val deletedJobIds: List<Int> = emptyList(),
    @Json(name = "server_time") val serverTime: String,
    @Json(name = "organisations") val organisations: List<OrganisationDto>? = null
)
