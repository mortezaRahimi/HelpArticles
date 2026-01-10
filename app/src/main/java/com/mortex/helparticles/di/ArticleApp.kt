package com.mortex.helparticles.di

import android.app.Application
import androidx.work.Configuration
import com.mortex.helparticles.util.CachePolicy
import com.mortex.helparticles.work.PrefetchNotifications
import com.mortex.helparticles.work.WorkScheduler
import com.mortex.helparticles.work.WorkerFactory

class ArticleApp() : Application() , Configuration.Provider{

    val container: AppContainer by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        AppContainer(
            context = this,
            ttlMillis = CachePolicy.ARTICLE_TTL_MS
        )
    }

    override fun onCreate() {
        super.onCreate()
        PrefetchNotifications.ensureChannel(this)
        WorkScheduler.scheduleDailyPrefetch(this)
    }

    override val workManagerConfiguration: Configuration
        get() {
            return Configuration.Builder()
                .setWorkerFactory(WorkerFactory(container))
                .build()
        }


}