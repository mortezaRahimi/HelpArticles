package com.mortex.helparticles.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Schedules the daily article prefetch using WorkManager.
 */
object WorkScheduler {

    fun scheduleDailyPrefetch(context: Context) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)     // only on network
            .setRequiresBatteryNotLow(true)                    // avoid battery drain
            .build()

        val request = PeriodicWorkRequestBuilder<PrefetchWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInitialDelay(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "15min_article_prefetch",
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }
}
