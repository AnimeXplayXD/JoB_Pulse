package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey val id: Int,
    val slug: String? = null,
    val title: String,
    val organisationId: String? = null,
    val organization: String,
    val category: String,
    val shortDescription: String? = null,
    val description: String? = null,
    val employmentType: String? = null,
    val department: String? = null,
    val vacancies: Int? = null,
    val educationalQualification: String? = null,
    val minAge: Int? = null,
    val maxAge: Int? = null,
    val ageRelaxation: String? = null,
    val payScale: String? = null,
    val applicationStartDate: String? = null,
    val applicationEndDate: String? = null,
    val examDate: String? = null,
    val admitCardDate: String? = null,
    val resultDate: String? = null,
    val examMode: String? = null,
    val examLocation: String? = null,
    val postingLocation: String? = null,
    val selectionProcessJson: String? = null,
    val syllabusJson: String? = null,
    val reservationInformation: String? = null,
    val cutoffInformation: String? = null,
    val applicationUrl: String? = null,
    val officialNotificationUrl: String? = null,
    val officialSourceUrl: String? = null,
    val sourceName: String? = null,
    val isOfficialSource: Boolean = true,
    val status: String,
    val publishedAt: String? = null,
    val lastUpdatedAt: String? = null,
    val lastVerifiedAt: String? = null,
    val isDeleted: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "organisations")
data class OrganisationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val shortName: String,
    val type: String? = null,
    val officialWebsite: String? = null,
    val officialRecruitmentUrl: String? = null,
    val brandingId: String? = null,
    val lastVerifiedAt: String? = null
)

@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey val syncKey: String,
    val lastSyncTimestamp: String,
    val lastSyncLocalTime: Long = System.currentTimeMillis()
)
