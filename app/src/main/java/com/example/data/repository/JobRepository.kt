package com.example.data.repository

import com.example.model.Job
import com.example.model.Organisation
import kotlinx.coroutines.flow.Flow

interface JobRepository {
    fun getJobsStream(): Flow<List<Job>>
    fun getJobByIdStream(id: Int): Flow<Job?>
    fun getOrganisationsStream(): Flow<List<Organisation>>
    fun searchJobsStream(query: String): Flow<List<Job>>
    suspend fun refresh(force: Boolean = false): Result<Unit>
}
