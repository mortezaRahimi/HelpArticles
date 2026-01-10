package com.mortex.helparticles.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.mortex.helparticles.di.AppContainer

class WorkerFactory(
    private val container: AppContainer
) : androidx.work.WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        return when (workerClassName) {
            PrefetchWorker::class.java.name -> PrefetchWorker(
                appContext = appContext,
                params = workerParameters,
                refreshArticlesIfStaleUseCase = container.refreshArticlesIfStaleUseCase
            )
            else -> null
        }
    }
}