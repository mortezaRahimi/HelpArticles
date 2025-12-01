package com.mortex.helparticles.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mortex.helparticles.presentation.ui.articles.ArticlesViewModel
import com.mortex.helparticles.presentation.ui.details.ArticleDetailViewModel

/**
 * Simple ViewModel factory that uses AppContainer to provide dependencies.
 */
class ViewModelFactory(
    private val container: AppContainer,
    private val articleId: String? = null,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return when (modelClass) {

            ArticlesViewModel::class.java -> {
                ArticlesViewModel(
                    getArticlesUseCase = container.getArticlesUseCase,
                    refreshArticlesIfStaleUseCase = container.refreshArticlesIfStaleUseCase,
                    networkStatusUseCase = container.observeNetworkStatusUseCase
                ) as T
            }

            ArticleDetailViewModel::class.java -> {
                val id = articleId
                    ?: throw IllegalArgumentException("ArticleDetailViewModel requires articleId")

                ArticleDetailViewModel(
                    articleId = id,
                    getArticleDetailUseCase = container.getArticleDetailUseCase
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        }
    }
}
