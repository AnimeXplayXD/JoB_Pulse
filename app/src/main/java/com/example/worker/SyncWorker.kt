package com.example.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.JobPulseApp
import com.example.data.repository.JobRepositoryProvider
import com.example.util.NotificationHelper

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val repository = JobRepositoryProvider.getRepository(applicationContext as JobPulseApp)
        
        return try {
            val result = repository.refresh(force = true)
            if (result.isSuccess) {
                // In a production environment, we would check if there are actual new jobs
                // by comparing the old and new cache. For now, we demonstrate delivery.
                NotificationHelper.postNewJobNotification(applicationContext)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
