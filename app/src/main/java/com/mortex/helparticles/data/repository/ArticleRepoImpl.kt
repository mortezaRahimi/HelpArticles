package com.mortex.helparticles.data.repository


import com.mortex.helparticles.data.mapper.ArticleMapper
import com.mortex.helparticles.data.network.NetworkChecker
import com.mortex.helparticles.data.remote.ConnectivityException
import com.mortex.helparticles.data.remote.FakeRemoteDataSrc
import com.mortex.helparticles.domain.model.ArticleDetail
import com.mortex.helparticles.domain.model.ArticleSummary
import com.mortex.helparticles.domain.repository.ArticlesRepository
import com.mortex.helparticles.util.AppError
import com.mortex.helparticles.util.AppResult
import com.mortex.helparticles.util.NetworkErrorMapper
import com.mortex.shared.cache.ArticleCache

/**
 * Concrete implementation of ArticlesRepository.
 *
 * Responsibilities:
 * - decide when to use cache vs remote
 * - use KMP shared ArticleCache
 * - map DTO -> domain
 * - map exceptions -> AppError
 *
 * Remote is backed by an OkHttp client with MockHelpArticlesInterceptor,
 * so all calls go through a self-contained mock HTTP backend.
 */
class ArticleRepoImpl(
    private val remote: FakeRemoteDataSrc,
    private val cache: ArticleCache,
    private val networkChecker: NetworkChecker
) : ArticlesRepository {

    override suspend fun getArticles(): AppResult<List<ArticleSummary>> {

        if (!networkChecker.isOnline()) {
            val cached = cache.getFreshList<ArticleSummary>()
            return if (cached != null && cached.isNotEmpty()) {
                AppResult.Success(cached, isOnline = false, fromCache = true)
            } else {
                AppResult.Error(
                    NetworkErrorMapper.toAppError(ConnectivityException("Internet connection lost :("))
                )
            }
        }
        return try {
            // Always try remote; KMP cache decides staleness.
            val dtos = remote.fetchArticleSummaries()
            val domainList = dtos.map { ArticleMapper.toDomainSummary(it) }

            // Save into cache (domain-level objects)
            cache.saveList(domainList)

            AppResult.Success(
                data = domainList,
                isOnline = true,
                fromCache = false
            )
        } catch (t: Throwable) {
            // On any failure, fall back to cache first
            val cached = cache.getFreshList<ArticleSummary>()
            if (!cached.isNullOrEmpty()) {
                val appError = NetworkErrorMapper.toAppError(t)
                val isOnline = appError !is AppError.Connectivity

                // We have valid cached data, so serve it but mark origin
                return AppResult.Success(
                    data = cached,
                    isOnline = isOnline,   // false for connectivity, true for backend errors
                    fromCache = true
                )
            }

            // No cache => propagate mapped error
            val appError: AppError = NetworkErrorMapper.toAppError(t)
            AppResult.Error(appError)
        }
    }

    override suspend fun getArticleDetail(
        articleId: String,
    ): AppResult<ArticleDetail> {

        if (!networkChecker.isOnline()) {
            val cached = cache.getFreshDetail<ArticleDetail>(articleId)
            return if (cached != null) {
                AppResult.Success(cached, isOnline = false, fromCache = true)
            } else {
                AppResult.Error(
                    NetworkErrorMapper.toAppError(ConnectivityException("No Internet connection"))
                )
            }
        }

        return try {
            val dto = remote.fetchArticleDetail(articleId)
            val domain = ArticleMapper.toDomainDetail(dto)

            cache.saveDetail(articleId, domain)

            AppResult.Success(
                data = domain,
                isOnline = true,
                fromCache = false
            )
        } catch (t: Throwable) {
            // Try cached detail as a safe fallback
            val cached = cache.getFreshDetail<ArticleDetail>(articleId)
            if (cached != null) {
                val appError = NetworkErrorMapper.toAppError(t)
                val isOnline = appError !is AppError.Connectivity

                return AppResult.Success(
                    data = cached,
                    isOnline = isOnline,   // false when we know it was a connectivity problem
                    fromCache = true
                )
            }

            val appError = NetworkErrorMapper.toAppError(t)
            AppResult.Error(appError)
        }
    }

    override suspend fun refreshArticlesIfStale(): AppResult<List<ArticleSummary>> {
        // By design, ArticleCache returns null if stale or missing
        val cached = cache.getFreshList<ArticleSummary>()
        if (!cached.isNullOrEmpty()) {
            // Not stale -> nothing to do; return what we have
            return AppResult.Success(
                data = cached,
                fromCache = true,
                isOnline = false // "unknown" here, but we didn't hit network this time
            )
        }

        // Stale or missing -> try to fetch fresh
        return try {
            val dtos = remote.fetchArticleSummaries()
            val domainList = dtos.map { ArticleMapper.toDomainSummary(it) }
            cache.saveList(domainList)

            AppResult.Success(
                data = domainList,
                fromCache = false,
                isOnline = true
            )
        } catch (t: Throwable) {
            // Auto-refresh is best-effort; propagate error
            val appError = NetworkErrorMapper.toAppError(t)
            AppResult.Error(appError)
        }
    }
}
