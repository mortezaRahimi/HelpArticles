package com.mortex.helparticles.domain.usecase

import com.mortex.helparticles.domain.model.ArticleSummary
import com.mortex.helparticles.domain.repository.ArticlesRepository
import com.mortex.helparticles.util.AppResult

/**
 * Intended for:
 * - app resume auto-refresh
 * - background prefetch via WorkManager
 *
 * Implementation may:
 * - check cache staleness (in repository / KMP cache)
 * - fetch new data only if needed
 */
class RefreshArticlesIfStaleUseCase(
    private val repository: ArticlesRepository,
) {

    suspend operator fun invoke(): AppResult<List<ArticleSummary>> {
        return repository.refreshArticlesIfStale()
    }
}