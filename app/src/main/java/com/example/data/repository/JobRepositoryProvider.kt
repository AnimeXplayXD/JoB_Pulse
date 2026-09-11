package com.example.data.repository

import android.content.Context
import com.example.data.local.RecruitmentDatabase
import com.example.data.remote.DevelopmentJobDataSource
import com.example.data.remote.RecruitmentApiService
import com.example.data.remote.RemoteJobDataSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object JobRepositoryProvider {

    @Volatile
    private var repositoryInstance: JobRepository? = null

    /**
     * Provide JobRepository singleton instance.
     * By default uses DevelopmentJobDataSource unless configured with a custom RemoteJobDataSource.
     */
    fun getRepository(
        context: Context,
        customRemoteDataSource: RemoteJobDataSource? = null
    ): JobRepository {
        return repositoryInstance ?: synchronized(this) {
            repositoryInstance ?: createRepository(context, customRemoteDataSource).also {
                repositoryInstance = it
            }
        }
    }

    private fun createRepository(
        context: Context,
        customRemoteDataSource: RemoteJobDataSource?
    ): JobRepository {
        val database = RecruitmentDatabase.getInstance(context)
        val remoteSource = customRemoteDataSource ?: DevelopmentJobDataSource()

        return OfflineFirstJobRepository(
            database = database,
            jobDao = database.jobDao(),
            organisationDao = database.organisationDao(),
            syncMetadataDao = database.syncMetadataDao(),
            remoteDataSource = remoteSource
        )
    }

    /**
     * Helper to build a live Retrofit service if a backend base URL is provided.
     */
    fun createRetrofitService(baseUrl: String): RecruitmentApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RecruitmentApiService::class.java)
    }

    /**
     * Resets repository instance (used primarily in tests).
     */
    fun resetForTesting(testRepository: JobRepository? = null) {
        repositoryInstance = testRepository
    }
}
