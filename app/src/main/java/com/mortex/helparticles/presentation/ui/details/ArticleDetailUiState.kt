package com.mortex.helparticles.presentation.ui.details

import com.mortex.helparticles.domain.model.ArticleDetail
import com.mortex.helparticles.util.AppError

data class ArticleDetailUiState(
    val isLoading: Boolean = false,
    val article: ArticleDetail? = null,
    val error: AppError? = null,
    val isOnline: Boolean = false,
)