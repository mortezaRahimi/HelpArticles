package com.mortex.helparticles.domain.usecase

import com.mortex.helparticles.domain.model.ArticleDetail
import com.mortex.helparticles.domain.repository.ArticlesRepository
import com.mortex.helparticles.util.AppResult


/**
 * Loads a single article's detail content.
 */
class GetArticleDetailUseCase(
    private val repository: ArticlesRepository
) {

    suspend operator fun invoke(
        articleId: String
    ): AppResult<ArticleDetail> {
        return repository.getArticleDetail(
            articleId = articleId
        )
    }
}