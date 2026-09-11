package com.example

import com.example.data.local.JobDao
import com.example.data.local.JobEntity
import com.example.data.local.OrganisationDao
import com.example.data.local.OrganisationEntity
import com.example.data.local.RecruitmentDatabase
import com.example.data.local.SyncMetadataDao
import com.example.data.local.SyncMetadataEntity
import com.example.data.remote.JobDto
import com.example.data.remote.JobsSyncResponseDto
import com.example.data.remote.OrganisationDto
import com.example.data.remote.RemoteJobDataSource
import com.example.data.repository.OfflineFirstJobRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class JobRepositoryTest {

    class FakeJobDao : JobDao {
        val jobs = mutableMapOf<Int, JobEntity>()
        val jobsFlow = MutableStateFlow<List<JobEntity>>(emptyList())

        private fun emit() {
            jobsFlow.value = jobs.values.filter { !it.isDeleted }.toList()
        }

        override fun getJobsFlow(): Flow<List<JobEntity>> = jobsFlow

        override fun searchJobsFlow(query: String): Flow<List<JobEntity>> = jobsFlow

        override fun getJobByIdFlow(id: Int): Flow<JobEntity?> {
            return MutableStateFlow(jobs[id]?.takeIf { !it.isDeleted })
        }

        override suspend fun getJobById(id: Int): JobEntity? = jobs[id]?.takeIf { !it.isDeleted }

        override suspend fun upsertJobs(jobs: List<JobEntity>) {
            jobs.forEach { this.jobs[it.id] = it }
            emit()
        }

        override suspend fun upsertJob(job: JobEntity) {
            jobs[job.id] = job
            emit()
        }

        override suspend fun softDeleteJobs(jobIds: List<Int>) {
            jobIds.forEach { id ->
                jobs[id]?.let { jobs[id] = it.copy(isDeleted = true) }
            }
            emit()
        }

        override suspend fun hardDeleteJobs(jobIds: List<Int>) {
            jobIds.forEach { jobs.remove(it) }
            emit()
        }

        override suspend fun clearAllJobs() {
            jobs.clear()
            emit()
        }

        override suspend fun getActiveJobCount(): Int = jobs.values.count { !it.isDeleted }
    }

    class FakeOrganisationDao : OrganisationDao {
        val orgs = mutableMapOf<String, OrganisationEntity>()
        val orgsFlow = MutableStateFlow<List<OrganisationEntity>>(emptyList())

        override fun getOrganisationsFlow(): Flow<List<OrganisationEntity>> = orgsFlow

        override suspend fun getOrganisationById(id: String): OrganisationEntity? = orgs[id]

        override suspend fun upsertOrganisations(orgs: List<OrganisationEntity>) {
            orgs.forEach { this.orgs[it.id] = it }
            orgsFlow.value = this.orgs.values.toList()
        }

        override suspend fun clearAllOrganisations() {
            orgs.clear()
            orgsFlow.value = emptyList()
        }
    }

    class FakeSyncMetadataDao : SyncMetadataDao {
        private val metadata = mutableMapOf<String, SyncMetadataEntity>()

        override suspend fun getMetadata(key: String): SyncMetadataEntity? = metadata[key]

        override suspend fun setMetadata(metadata: SyncMetadataEntity) {
            this.metadata[metadata.syncKey] = metadata
        }

        override suspend fun clearMetadata(key: String) {
            metadata.remove(key)
        }
    }

    class FakeRemoteDataSource(
        var syncResponse: JobsSyncResponseDto = JobsSyncResponseDto(
            jobs = emptyList(),
            deletedJobIds = emptyList(),
            serverTime = "2026-09-12T12:00:00Z",
            organisations = emptyList()
        ),
        var shouldFail: Boolean = false
    ) : RemoteJobDataSource {
        var lastUpdatedSinceRequested: String? = null

        override suspend fun fetchJobsSync(updatedSince: String?): JobsSyncResponseDto {
            lastUpdatedSinceRequested = updatedSince
            if (shouldFail) throw IOException("Remote network error")
            return syncResponse
        }

        override suspend fun fetchJobById(id: Int): JobDto? {
            if (shouldFail) throw IOException("Remote network error")
            return syncResponse.jobs.find { it.id == id }
        }

        override suspend fun fetchOrganisations(): List<OrganisationDto> {
            if (shouldFail) throw IOException("Remote network error")
            return syncResponse.organisations ?: emptyList()
        }
    }

    @Test
    fun refresh_upsertsNewJobs_andUpdatesMetadata() = runTest {
        val jobDao = FakeJobDao()
        val orgDao = FakeOrganisationDao()
        val syncDao = FakeSyncMetadataDao()
        val remoteSource = FakeRemoteDataSource(
            syncResponse = JobsSyncResponseDto(
                jobs = listOf(
                    JobDto(
                        id = 1,
                        title = "Assistant Executive Officer",
                        organisationId = "upsc",
                        organization = "UPSC",
                        category = "CENTRAL",
                        vacancies = 450,
                        status = "APPLICATION_OPEN"
                    )
                ),
                deletedJobIds = emptyList(),
                serverTime = "2026-09-12T10:00:00Z",
                organisations = listOf(
                    OrganisationDto(id = "upsc", name = "UPSC", shortName = "UPSC")
                )
            )
        )

        val repository = OfflineFirstJobRepository(
            database = null,
            jobDao = jobDao,
            organisationDao = orgDao,
            syncMetadataDao = syncDao,
            remoteDataSource = remoteSource
        )

        val result = repository.refresh(force = false)
        assertTrue(result.isSuccess)

        // Verify that remote data was passed to DAO
        assertEquals(1, jobDao.getActiveJobCount())
        val jobs = repository.getJobsStream().first()
        assertEquals(1, jobs.size)
        assertEquals("Assistant Executive Officer", jobs.first().title)
        assertEquals("upsc", jobs.first().organisationId)

        // Verify sync metadata updated
        val metadata = syncDao.getMetadata("jobs_sync")
        assertNotNull(metadata)
        assertEquals("2026-09-12T10:00:00Z", metadata?.lastSyncTimestamp)
    }

    @Test
    fun refresh_handlesDeltaSync_passingUpdatedSince() = runTest {
        val jobDao = FakeJobDao()
        val orgDao = FakeOrganisationDao()
        val syncDao = FakeSyncMetadataDao()
        syncDao.setMetadata(SyncMetadataEntity("jobs_sync", "2026-09-10T00:00:00Z"))

        // Add 1 active job to simulate pre-existing local cache
        jobDao.upsertJob(
            JobEntity(
                id = 99,
                title = "Existing Officer",
                organization = "Railways",
                category = "RAILWAY",
                status = "APPLICATION_OPEN"
            )
        )

        val remoteSource = FakeRemoteDataSource()
        val repository = OfflineFirstJobRepository(
            database = null,
            jobDao = jobDao,
            organisationDao = orgDao,
            syncMetadataDao = syncDao,
            remoteDataSource = remoteSource
        )

        // Delta sync: force = false
        repository.refresh(force = false)
        assertEquals("2026-09-10T00:00:00Z", remoteSource.lastUpdatedSinceRequested)

        // Force full refresh: force = true
        repository.refresh(force = true)
        assertNull(remoteSource.lastUpdatedSinceRequested)
    }

    @Test
    fun refresh_deletesJobsSpecifiedInResponse() = runTest {
        val jobDao = FakeJobDao()
        val orgDao = FakeOrganisationDao()
        val syncDao = FakeSyncMetadataDao()

        jobDao.upsertJob(
            JobEntity(
                id = 10,
                title = "To be deleted",
                organization = "SSC",
                category = "CENTRAL",
                status = "APPLICATION_OPEN"
            )
        )
        assertEquals(1, jobDao.getActiveJobCount())

        val remoteSource = FakeRemoteDataSource(
            syncResponse = JobsSyncResponseDto(
                jobs = emptyList(),
                deletedJobIds = listOf(10),
                serverTime = "2026-09-12T12:00:00Z"
            )
        )
        val repository = OfflineFirstJobRepository(
            database = null,
            jobDao = jobDao,
            organisationDao = orgDao,
            syncMetadataDao = syncDao,
            remoteDataSource = remoteSource
        )

        repository.refresh(force = false)
        assertEquals(0, jobDao.getActiveJobCount())
    }

    @Test
    fun refresh_networkFailure_returnsFailure_preservesCache() = runTest {
        val jobDao = FakeJobDao()
        val orgDao = FakeOrganisationDao()
        val syncDao = FakeSyncMetadataDao()

        jobDao.upsertJob(
            JobEntity(
                id = 1,
                title = "Existing Cached Notice",
                organization = "SBI",
                category = "BANK",
                status = "APPLICATION_OPEN"
            )
        )

        val remoteSource = FakeRemoteDataSource(shouldFail = true)
        val repository = OfflineFirstJobRepository(
            database = null,
            jobDao = jobDao,
            organisationDao = orgDao,
            syncMetadataDao = syncDao,
            remoteDataSource = remoteSource
        )

        val result = repository.refresh(force = false)
        assertTrue(result.isFailure)

        // Cache remains completely intact!
        assertEquals(1, jobDao.getActiveJobCount())
        val cachedJobs = repository.getJobsStream().first()
        assertEquals("Existing Cached Notice", cachedJobs.first().title)
    }
}
