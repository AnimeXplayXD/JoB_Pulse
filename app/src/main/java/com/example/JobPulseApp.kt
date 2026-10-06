package com.example

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.repository.JobRepositoryProvider
import com.example.worker.SyncWorker
import java.util.concurrent.TimeUnit

class JobPulseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Pre-initialize repository
        JobRepositoryProvider.getRepository(this)
        
        setupBackgroundSync()
    }

    private fun setupBackgroundSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(12, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
            
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "JobPulseSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    companion object {
        lateinit var instance: JobPulseApp
            internal set
    }
}
