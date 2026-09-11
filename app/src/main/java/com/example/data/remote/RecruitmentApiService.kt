package com.example.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecruitmentApiService {

    @GET("api/v1/jobs")
    suspend fun getJobs(
        @Query("updated_since") updatedSince: String? = null,
        @Query("organisation_id") organisationId: String? = null,
        @Query("status") status: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<JobsSyncResponseDto>

    @GET("api/v1/jobs/{id}")
    suspend fun getJobById(
        @Path("id") id: Int
    ): Response<JobDto>

    @GET("api/v1/organisations")
    suspend fun getOrganisations(): Response<List<OrganisationDto>>
}
