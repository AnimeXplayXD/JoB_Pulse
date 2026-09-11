package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {

    @Query("SELECT * FROM jobs WHERE isDeleted = 0 ORDER BY id DESC")
    fun getJobsFlow(): Flow<List<JobEntity>>

    @Query("""
        SELECT * FROM jobs 
        WHERE isDeleted = 0 
          AND (
            title LIKE '%' || :query || '%' 
            OR organization LIKE '%' || :query || '%' 
            OR department LIKE '%' || :query || '%'
            OR category LIKE '%' || :query || '%'
          )
        ORDER BY id DESC
    """)
    fun searchJobsFlow(query: String): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id AND isDeleted = 0 LIMIT 1")
    fun getJobByIdFlow(id: Int): Flow<JobEntity?>

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    suspend fun getJobById(id: Int): JobEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertJobs(jobs: List<JobEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertJob(job: JobEntity)

    @Query("UPDATE jobs SET isDeleted = 1 WHERE id IN (:jobIds)")
    suspend fun softDeleteJobs(jobIds: List<Int>)

    @Query("DELETE FROM jobs WHERE id IN (:jobIds)")
    suspend fun hardDeleteJobs(jobIds: List<Int>)

    @Query("DELETE FROM jobs")
    suspend fun clearAllJobs()

    @Query("SELECT COUNT(*) FROM jobs WHERE isDeleted = 0")
    suspend fun getActiveJobCount(): Int
}

@Dao
interface OrganisationDao {

    @Query("SELECT * FROM organisations ORDER BY name ASC")
    fun getOrganisationsFlow(): Flow<List<OrganisationEntity>>

    @Query("SELECT * FROM organisations WHERE id = :id LIMIT 1")
    suspend fun getOrganisationById(id: String): OrganisationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOrganisations(orgs: List<OrganisationEntity>)

    @Query("DELETE FROM organisations")
    suspend fun clearAllOrganisations()
}

@Dao
interface SyncMetadataDao {

    @Query("SELECT * FROM sync_metadata WHERE syncKey = :key LIMIT 1")
    suspend fun getMetadata(key: String): SyncMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setMetadata(metadata: SyncMetadataEntity)

    @Query("DELETE FROM sync_metadata WHERE syncKey = :key")
    suspend fun clearMetadata(key: String)
}
