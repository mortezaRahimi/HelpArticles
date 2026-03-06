package com.mortex.helparticles.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mortex.helparticles.domain.usecase.RefreshArticlesIfStaleUseCase
import com.mortex.helparticles.util.AppResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Background worker that refreshes the article list once per day.
 */
class PrefetchWorker(
    appContext: Context,
    params: WorkerParameters,
    private val refreshArticlesIfStaleUseCase: RefreshArticlesIfStaleUseCase,

    ) : CoroutineWorker(appContext, params) {


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("PrefetchWorker", "called")
        val result = refreshArticlesIfStaleUseCase()
        Log.d("PrefetchWorker", "result.isSuccess = ${result.isSuccess}")
        val success = result is AppResult.Success

        if (success) {
            PrefetchNotifications.showSuccess(applicationContext)
            Result.success()
        } else {
            Result.retry()
        }
    }
}
