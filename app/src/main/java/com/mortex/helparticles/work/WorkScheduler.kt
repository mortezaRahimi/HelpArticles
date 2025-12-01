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

        val request = PeriodicWorkRequestBuilder<PrefetchWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_article_prefetch",
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }
}
