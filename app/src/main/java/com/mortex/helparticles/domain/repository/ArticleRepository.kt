package com.mortex.helparticles.domain.repository

import com.mortex.helparticles.domain.model.ArticleDetail
import com.mortex.helparticles.domain.model.ArticleSummary
import com.mortex.helparticles.util.AppResult

/**
 * Domain-facing contract for accessing help articles.
 *
 * Implementations will:
 * - Decide cache vs network
 * - Use the KMP cache module
 * - Map errors to AppError/AppResult
 */
interface ArticlesRepository {

    /**
     * Returns a list of help articles.
     */
    suspend fun getArticles(): AppResult<List<ArticleSummary>>

    /**
     * Returns full article detail for a given ID.
     */
    suspend fun getArticleDetail(
        articleId: String
    ): AppResult<ArticleDetail>

    /**
     * Optionally used by background workers or auto-refresh logic.
     * Tries to refresh the list when cache is stale.
     */
    suspend fun refreshArticlesIfStale(): AppResult<List<ArticleSummary>>
}