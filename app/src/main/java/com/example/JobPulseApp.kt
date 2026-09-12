package com.example

import android.app.Application
import com.example.data.repository.JobRepositoryProvider

class JobPulseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Pre-initialize repository
        JobRepositoryProvider.getRepository(this)
    }

    companion object {
        lateinit var instance: JobPulseApp
            internal set
    }
}
