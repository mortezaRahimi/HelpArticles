package com.mortex.helparticles.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mortex.helparticles.di.AppContainer
import com.mortex.helparticles.util.AppResult
import com.mortex.helparticles.util.CachePolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Background worker that refreshes the article list once per day.
 */
class PrefetchWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    private val container = AppContainer(
        ttlMillis = CachePolicy.ARTICLE_TTL_MS, // 1 Min TTL
        appContext
    )

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("PrefetchWorker", "called")
        val result = container.refreshArticlesIfStaleUseCase()
        Log.d("PrefetchWorker", "result.isSuccess = ${result.isSuccess}")
        val success = result is AppResult.Success
        return@withContext if (success) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
