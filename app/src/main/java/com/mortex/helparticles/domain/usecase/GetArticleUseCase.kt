package com.mortex.helparticles.domain.usecase

import com.mortex.helparticles.domain.model.ArticleSummary
import com.mortex.helparticles.domain.repository.ArticlesRepository
import com.mortex.helparticles.util.AppResult

/**
 * Loads the list of articles, respecting cache policy.
 */
class GetArticlesUseCase(
    private val repository: ArticlesRepository
) {

    suspend operator fun invoke(): AppResult<List<ArticleSummary>> {
        return repository.getArticles()
    }
}