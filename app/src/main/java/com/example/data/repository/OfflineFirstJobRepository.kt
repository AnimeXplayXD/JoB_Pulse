package com.example.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.example.data.local.JobDao
import com.example.data.local.OrganisationDao
import com.example.data.local.RecruitmentDatabase
import com.example.data.local.SyncMetadataDao
import com.example.data.local.SyncMetadataEntity
import com.example.data.remote.RemoteJobDataSource
import com.example.model.Job
import com.example.model.Organisation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineFirstJobRepository(
    private val database: RecruitmentDatabase? = null,
    private val jobDao: JobDao,
    private val organisationDao: OrganisationDao,
    private val syncMetadataDao: SyncMetadataDao,
    private val remoteDataSource: RemoteJobDataSource,
    private val transactionRunner: suspend (suspend () -> Unit) -> Unit = { block ->
        database?.withTransaction { block() } ?: block()
    }
) : JobRepository {

    companion object {
        private const val TAG = "JobRepository"
        private const val SYNC_KEY_JOBS = "jobs_sync"
    }

    override fun getJobsStream(): Flow<List<Job>> {
        return jobDao.getJobsFlow().map { entities ->
            entities.map { JobMappers.toDomain(it) }
        }
    }

    override fun getJobByIdStream(id: Int): Flow<Job?> {
        return jobDao.getJobByIdFlow(id).map { entity ->
            entity?.let { JobMappers.toDomain(it) }
        }
    }

    override fun getOrganisationsStream(): Flow<List<Organisation>> {
        return organisationDao.getOrganisationsFlow().map { entities ->
            entities.map { JobMappers.toDomain(it) }
        }
    }

    override fun searchJobsStream(query: String): Flow<List<Job>> {
        return jobDao.searchJobsFlow(query).map { entities ->
            entities.map { JobMappers.toDomain(it) }
        }
    }

    private fun logDebug(message: String) {
        try {
            android.util.Log.d(TAG, message)
        } catch (_: Exception) {
            println("[$TAG] $message")
        }
    }

    private fun logError(message: String, throwable: Throwable? = null) {
        try {
            android.util.Log.e(TAG, message, throwable)
        } catch (_: Exception) {
            println("[$TAG] ERROR: $message")
        }
    }

    override suspend fun refresh(force: Boolean): Result<Unit> {
        return try {
            val activeCount = jobDao.getActiveJobCount()
            val lastSync = syncMetadataDao.getMetadata(SYNC_KEY_JOBS)
            
            // If cache is empty, force full sync regardless
            val isInitial = activeCount == 0
            val updatedSince = if (force || isInitial) null else lastSync?.lastSyncTimestamp

            logDebug("Initiating sync: force=$force, isInitial=$isInitial, updatedSince=$updatedSince")

            val syncResponse = remoteDataSource.fetchJobsSync(updatedSince)

            transactionRunner {
                // 1. Process incoming or updated jobs
                if (syncResponse.jobs.isNotEmpty()) {
                    val entities = syncResponse.jobs.map { JobMappers.toEntity(it) }
                    jobDao.upsertJobs(entities)
                    logDebug("Upserted ${entities.size} jobs into Room cache")
                }

                // 2. Process deletions if any
                if (syncResponse.deletedJobIds.isNotEmpty()) {
                    jobDao.hardDeleteJobs(syncResponse.deletedJobIds)
                    logDebug("Removed ${syncResponse.deletedJobIds.size} deleted jobs from cache")
                }

                // 3. Process organisations if any
                syncResponse.organisations?.let { orgDtos ->
                    if (orgDtos.isNotEmpty()) {
                        val orgEntities = orgDtos.map { JobMappers.toEntity(it) }
                        organisationDao.upsertOrganisations(orgEntities)
                        logDebug("Upserted ${orgEntities.size} organisations into Room cache")
                    }
                }

                // 4. Update sync timestamp
                syncMetadataDao.setMetadata(
                    SyncMetadataEntity(
                        syncKey = SYNC_KEY_JOBS,
                        lastSyncTimestamp = syncResponse.serverTime,
                        lastSyncLocalTime = System.currentTimeMillis()
                    )
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            logError("Sync failed: ${e.message}. Preserving existing offline cache.", e)
            Result.failure(e)
        }
    }
}
