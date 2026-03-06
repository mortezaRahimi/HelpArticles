package com.mortex.helparticles.di

import android.content.Context
import com.mortex.helparticles.core.AppDispatchers
import com.mortex.helparticles.core.DefaultAppDispatchers
import com.mortex.helparticles.data.network.NetworkChecker
import com.mortex.helparticles.data.network.NetworkStatusMonitor
import com.mortex.helparticles.data.network.NetworkStatusMonitorDetector
import com.mortex.helparticles.data.remote.FakeRemoteDataSrc
import com.mortex.helparticles.data.remote.MockInterceptor
import com.mortex.helparticles.data.repository.ArticleRepoImpl
import com.mortex.helparticles.domain.usecase.GetArticleDetailUseCase
import com.mortex.helparticles.domain.usecase.GetArticlesUseCase
import com.mortex.helparticles.domain.usecase.ObserveNetworkStatusUseCase
import com.mortex.helparticles.domain.usecase.RefreshArticlesIfStaleUseCase
import com.mortex.helparticles.util.CachePolicy
import com.mortex.shared.cache.ArticleCache
import com.mortex.shared.cache.ArticleDB
import com.mortex.shared.cache.DataBaseFactory
import com.mortex.shared.util.DefaultTimeProvider
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

/**
 * Simple DI container.
 *
 * Uses:
 * - In-memory KmpCache
 * - ArticleCache with TTL + StalenessChecker
 * - FakeRemoteDataSource as mock http
 * - ArticlesRepositoryImpl as data entry point
 * - Use cases for the domain layer
 */
class AppContainer(
    ttlMillis: Long = CachePolicy.ARTICLE_TTL_MS,
    context: Context,
) {

    private val applicationContext = context.applicationContext

    private val dispatchers: AppDispatchers = DefaultAppDispatchers
    private val networkChecker = NetworkChecker(context)

    // Network monitor
    private val networkStatusMonitor: NetworkStatusMonitor =
        NetworkStatusMonitorDetector(context, networkChecker)

    // 1) Shared ArticleCache with TTL and TimeProvider

    private val db: ArticleDB = DataBaseFactory(applicationContext).createDataBase()

    // 2) Initialize the Cache (Wrapping the DB)
    val kmpCache: ArticleCache by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        ArticleCache(
            cache = db,
            ttlMillis = ttlMillis,
            timeProvider = DefaultTimeProvider
        )
    }


    // JSON serializer (kotlinx.serialization)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    // OkHttp with mock interceptor
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(MockInterceptor(json))
        .build()

    // 2) Fake remote backend
    // HTTP-based remote data source
    private val newRemote = FakeRemoteDataSrc(
        client = okHttpClient,
        json = json,
        appDispatchers = dispatchers
    )

    // 3) Repository
    private val repo = ArticleRepoImpl(
        remote = newRemote,
        cache = kmpCache,
        networkChecker
    )


    // 4) Use cases exposed to UI / ViewModels
    val getArticlesUseCase = GetArticlesUseCase(repo)
    val getArticleDetailUseCase = GetArticleDetailUseCase(repo)
    val refreshArticlesIfStaleUseCase = RefreshArticlesIfStaleUseCase(repo)
    val observeNetworkStatusUseCase = ObserveNetworkStatusUseCase(networkStatusMonitor)
}

