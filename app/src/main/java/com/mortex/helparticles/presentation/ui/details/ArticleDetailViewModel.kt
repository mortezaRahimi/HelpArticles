package com.mortex.helparticles.presentation.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mortex.helparticles.domain.usecase.GetArticleDetailUseCase
import com.mortex.helparticles.util.AppResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArticleDetailViewModel(
    private val articleId: String,
    private val getArticleDetailUseCase: GetArticleDetailUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState: StateFlow<ArticleDetailUiState> = _uiState

    init {
        loadDetail()
    }

    fun loadDetail() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            when (val result = getArticleDetailUseCase(articleId)) {
                is AppResult.Success -> {
                    _uiState.value = ArticleDetailUiState(
                        isLoading = false,
                        article = result.data,
                        error = null,
                        isOnline = result.isOnline
                    )
                }

                is AppResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.appError
                    )
                }
            }
        }
    }

    fun retry() {
        loadDetail()
    }

}
