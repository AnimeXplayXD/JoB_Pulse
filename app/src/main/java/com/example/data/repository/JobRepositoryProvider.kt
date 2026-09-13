package com.example.data.repository

import android.content.Context
import com.example.BuildConfig
import com.example.data.local.RecruitmentDatabase
import com.example.data.remote.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object JobRepositoryProvider {
    @Volatile private var repositoryInstance: JobRepository? = null
    fun getRepository(context: Context, customRemoteDataSource: RemoteJobDataSource? = null): JobRepository =
        repositoryInstance ?: synchronized(this) {
            repositoryInstance ?: createRepository(context, customRemoteDataSource).also { repositoryInstance = it }
        }
    private fun createRepository(context: Context, customRemote: RemoteJobDataSource?): JobRepository {
        val database = RecruitmentDatabase.getInstance(context)
        val remote = customRemote ?: if (BuildConfig.DEBUG) DemoDataSource() else UnconfiguredDataSource()
        return OfflineFirstJobRepository(database, database.jobDao(), database.organisationDao(), database.syncMetadataDao(), remote)
    }
    fun createRetrofitService(baseUrl: String): RecruitmentApiService {
        val client = OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) client.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        return Retrofit.Builder().baseUrl(baseUrl).client(client.build())
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build().create(RecruitmentApiService::class.java)
    }
    fun resetForTesting(testRepository: JobRepository? = null) { repositoryInstance = testRepository }
}

private class DemoDataSource : RemoteJobDataSource {
    private val delegate = DevelopmentJobDataSource()
    private fun JobDto.asDemo() = copy(isOfficialSource = false, sourceName = "Development sample", lastVerifiedAt = null)
    override suspend fun fetchJobsSync(updatedSince: String?): JobsSyncResponseDto {
        val result = delegate.fetchJobsSync(updatedSince)
        return result.copy(jobs = result.jobs.map { it.asDemo() }, organisations = result.organisations?.map { it.copy(lastVerifiedAt = null) })
    }
    override suspend fun fetchJobById(id: Int): JobDto? = delegate.fetchJobById(id)?.asDemo()
    override suspend fun fetchOrganisations(): List<OrganisationDto> = delegate.fetchOrganisations().map { it.copy(lastVerifiedAt = null) }
}

private class UnconfiguredDataSource : RemoteJobDataSource {
    override suspend fun fetchJobsSync(updatedSince: String?): JobsSyncResponseDto = throw IOException("Production recruitment service is not configured")
    override suspend fun fetchJobById(id: Int): JobDto? = throw IOException("Production recruitment service is not configured")
    override suspend fun fetchOrganisations(): List<OrganisationDto> = throw IOException("Production recruitment service is not configured")
}
