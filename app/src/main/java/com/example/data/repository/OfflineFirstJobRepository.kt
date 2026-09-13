package com.example.data.repository

import androidx.room.withTransaction
import com.example.data.local.*
import com.example.data.remote.RemoteJobDataSource
import com.example.model.Job
import com.example.model.Organisation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class OfflineFirstJobRepository(
    private val database: RecruitmentDatabase? = null,
    private val jobDao: JobDao,
    private val organisationDao: OrganisationDao,
    private val syncMetadataDao: SyncMetadataDao,
    private val remoteDataSource: RemoteJobDataSource,
    private val transactionRunner: suspend (suspend () -> Unit) -> Unit = { block -> database?.withTransaction { block() } ?: block() }
) : JobRepository {
    private val syncMutex = Mutex()
    override fun getJobsStream(): Flow<List<Job>> = jobDao.getJobsFlow().map { rows -> rows.map(JobMappers::toDomain) }.flowOn(Dispatchers.Default)
    override fun getJobByIdStream(id: Int): Flow<Job?> = jobDao.getJobByIdFlow(id).map { it?.let(JobMappers::toDomain) }.flowOn(Dispatchers.Default)
    override fun getOrganisationsStream(): Flow<List<Organisation>> = organisationDao.getOrganisationsFlow().map { rows -> rows.map(JobMappers::toDomain) }.flowOn(Dispatchers.Default)
    override fun searchJobsStream(query: String): Flow<List<Job>> = jobDao.searchJobsFlow(query).map { rows -> rows.map(JobMappers::toDomain) }.flowOn(Dispatchers.Default)
    override suspend fun refresh(force: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        syncMutex.withLock {
        try {
            val metadata = syncMetadataDao.getMetadata("jobs_sync")
            val since = if (force || jobDao.getActiveJobCount() == 0) null else metadata?.lastSyncTimestamp
            val response = remoteDataSource.fetchJobsSync(since)
            require(response.serverTime.isNotBlank()) { "Missing server sync cursor" }
            transactionRunner {
                if (response.jobs.isNotEmpty()) jobDao.upsertJobs(response.jobs.map(JobMappers::toEntity))
                if (response.deletedJobIds.isNotEmpty()) jobDao.hardDeleteJobs(response.deletedJobIds)
                response.organisations?.takeIf { it.isNotEmpty() }?.let { organisationDao.upsertOrganisations(it.map(JobMappers::toEntity)) }
                syncMetadataDao.setMetadata(SyncMetadataEntity("jobs_sync", response.serverTime, System.currentTimeMillis()))
            }
            Result.success(Unit)
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) { Result.failure(error) }
        }
    }
}
