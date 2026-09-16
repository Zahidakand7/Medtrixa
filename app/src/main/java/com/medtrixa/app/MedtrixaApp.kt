package com.medtrixa.app

import android.app.Application
import com.medtrixa.app.data.repository.BrandNameRepository
import com.medtrixa.app.data.repository.DrugInteractionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MedtrixaApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun onCreate() {
        super.onCreate()
        appScope.launch { DrugInteractionRepository.load(applicationContext) }
        appScope.launch { BrandNameRepository.load(applicationContext) }
    }
}
