package com.mortex.helparticles.presentation.ui.articles

import com.mortex.helparticles.domain.model.ArticleSummary
import com.mortex.helparticles.util.AppError

data class ArticlesUiState (
    val isLoading: Boolean = false,
    val articles: List<ArticleSummary> = emptyList(),
    val filteredArticles: List<ArticleSummary> = emptyList(),
    val searchQuery: String = "",
    val error: AppError? = null,
    val isOnline: Boolean = true,
)