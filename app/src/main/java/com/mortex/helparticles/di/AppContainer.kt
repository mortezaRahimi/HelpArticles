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
import com.mortex.shared.cache.ArticleCache
import com.mortex.shared.cache.KmpCache
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
    ttlMillis: Long,
    context: Context,
) {

    private val dispatchers: AppDispatchers = DefaultAppDispatchers
    private val networkChecker = NetworkChecker(context)

    // Network monitor
    private val networkStatusMonitor: NetworkStatusMonitor =
        NetworkStatusMonitorDetector(context, networkChecker)

    // 1) KMP in-memory cache
    private val kmpCache = KmpCache()

    // 2) Shared ArticleCache with TTL and TimeProvider
    private val articleCache = ArticleCache(
        cache = kmpCache,
        ttlMillis = ttlMillis,
        timeProvider = DefaultTimeProvider
    )


    // JSON serializer (kotlinx.serialization)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    // OkHttp with mock interceptor
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(MockInterceptor(json))
        .build()

    // 3) Fake remote backend
    // HTTP-based remote data source
    private val newRemote = FakeRemoteDataSrc(
        client = okHttpClient,
        json = json,
        appDispatchers = dispatchers
    )

    // 4) Repository
    private val repo = ArticleRepoImpl(
        remote = newRemote,
        cache = articleCache,
        networkChecker
    )


    // 5) Use cases exposed to UI / ViewModels
    val getArticlesUseCase = GetArticlesUseCase(repo)
    val getArticleDetailUseCase = GetArticleDetailUseCase(repo)
    val refreshArticlesIfStaleUseCase = RefreshArticlesIfStaleUseCase(repo)
    val observeNetworkStatusUseCase = ObserveNetworkStatusUseCase(networkStatusMonitor)
}

