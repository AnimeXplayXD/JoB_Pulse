package com.example

import com.example.data.local.JobEntity
import com.example.data.local.SyncMetadataEntity
import com.example.data.remote.*
import com.example.data.repository.OfflineFirstJobRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class RemoteSyncBodyTest {
    @Test fun emptyHttpSuccess_preservesServerCursorAndCache() = runTest {
        val service = object : RecruitmentApiService {
            override suspend fun getJobs(updatedSince: String?, organisationId: String?, status: String?, limit: Int?, offset: Int?): Response<JobsSyncResponseDto> = Response.success(null)
            override suspend fun getJobById(id: Int): Response<JobDto> = Response.success(null)
            override suspend fun getOrganisations(): Response<List<OrganisationDto>> = Response.success(emptyList())
        }
        val jobs = JobRepositoryTest.FakeJobDao()
        jobs.upsertJob(JobEntity(1, title = "Cached notice", organization = "Source", category = "CENTRAL", status = "PUBLISHED"))
        val metadata = JobRepositoryTest.FakeSyncMetadataDao()
        metadata.setMetadata(SyncMetadataEntity("jobs_sync", "server-cursor"))
        val repository = OfflineFirstJobRepository(
            jobDao = jobs, organisationDao = JobRepositoryTest.FakeOrganisationDao(), syncMetadataDao = metadata,
            remoteDataSource = RetrofitRemoteJobDataSource(service)
        )
        assertTrue(repository.refresh(false).isFailure)
        assertEquals("server-cursor", metadata.getMetadata("jobs_sync")?.lastSyncTimestamp)
        assertEquals(1, jobs.getActiveJobCount())
    }
}
