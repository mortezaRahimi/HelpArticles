package com.mortex.helparticles.presentation.ui.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mortex.helparticles.domain.model.ArticleSummary
import com.mortex.helparticles.domain.usecase.GetArticlesUseCase
import com.mortex.helparticles.domain.usecase.ObserveNetworkStatusUseCase
import com.mortex.helparticles.domain.usecase.RefreshArticlesIfStaleUseCase
import com.mortex.helparticles.util.AppResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ArticlesViewModel(
    private val getArticlesUseCase: GetArticlesUseCase,
    private val refreshArticlesIfStaleUseCase: RefreshArticlesIfStaleUseCase,
    private val networkStatusUseCase: ObserveNetworkStatusUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticlesUiState())
    val uiState: StateFlow<ArticlesUiState> = _uiState

    private var hasSeenOnlineState = false
    private var hasSeenOfflineState = false

    private var loadJob: Job? = null

    init {
        loadArticles()

        observeNetwork()
    }


    fun loadArticles() {
        if (loadJob?.isActive == true) return
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        loadJob = viewModelScope.launch {
            when (val result = getArticlesUseCase()) {
                is AppResult.Success -> {
                    applyList(result.data, result.isOnline)
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

    private fun observeNetwork() {
        viewModelScope.launch {
            networkStatusUseCase()
                .distinctUntilChanged()
                .collect { online ->
                if (online) {
                    if (hasSeenOnlineState || hasSeenOfflineState) {
                        // we've been online before, went offline, now back online
                        autoRefreshIfStale()
                    } else {
                        // first online emission
                        hasSeenOnlineState = true
                    }
                } else
                    hasSeenOfflineState = true
            }
        }
    }

    /**
     * Auto-refresh via Network Status if Stale.
     */
    fun autoRefreshIfStale() {
        if (loadJob?.isActive == true) return
       loadJob = viewModelScope.launch {
            val result = refreshArticlesIfStaleUseCase()
            if (result is AppResult.Success) {
                applyList(result.data, isOnline = result.isOnline)
            }
        }
    }

    private fun applyList(list: List<ArticleSummary>, isOnline: Boolean) {
        val query = _uiState.value.searchQuery
        val filtered = filter(list, query)

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            articles = list,
            filteredArticles = filtered,
            isOnline = isOnline,
            error = null
        )
    }

    fun onSearchQueryChanged(query: String) {
        val filtered = filter(_uiState.value.articles, query)

        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredArticles = filtered
        )
    }

    private fun filter(list: List<ArticleSummary>, query: String): List<ArticleSummary> {
        if (query.isBlank()) return list

        return list.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.summary.contains(query, ignoreCase = true)
        }
    }

    fun retry() {
        loadArticles()
    }

}