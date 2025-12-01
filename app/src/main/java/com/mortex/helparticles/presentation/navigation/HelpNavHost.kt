package com.mortex.helparticles.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mortex.helparticles.di.AppContainer
import com.mortex.helparticles.di.ViewModelFactory
import com.mortex.helparticles.presentation.ui.articles.ArticlesScreen
import com.mortex.helparticles.presentation.ui.articles.ArticlesViewModel
import com.mortex.helparticles.presentation.ui.details.ArticleDetailScreen
import com.mortex.helparticles.presentation.ui.details.ArticleDetailViewModel

@Composable
fun HelpNavHost(
    padding: PaddingValues,
    navController: NavHostController,
    container: AppContainer
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Articles.route,
        modifier = Modifier.padding(padding)
    ) {


        // Articles list screen
        // ----------------------------------------------------
        composable(route = Screen.Articles.route) { backStackEntry ->

            // ViewModel factory scoped to this back stack entry
            val factory = remember(backStackEntry) {
                ViewModelFactory(container = container)
            }

            val viewModel: ArticlesViewModel = viewModel(factory = factory)
            val uiState by viewModel.uiState.collectAsState()

            ArticlesScreen(
                uiState = uiState,
                onArticleClick = { articleId ->
                    navController.navigate(Screen.ArticleDetail.createRoute(articleId))
                },
                onRetry = { viewModel.retry() },
                onSearch = { query -> viewModel.onSearchQueryChanged(query) }
            )
        }


        // Article detail screen
        // ----------------------------------------------------
        composable(route = Screen.ArticleDetail.route) { backStackEntry ->

            val articleId = backStackEntry.arguments?.getString("id")
                ?: return@composable

            val factory = remember(articleId) {
                ViewModelFactory(
                    container = container,
                    articleId = articleId
                )
            }

            val viewModel: ArticleDetailViewModel = viewModel(factory = factory)
            val uiState by viewModel.uiState.collectAsState()

            ArticleDetailScreen(
                uiState = uiState,
                onRetry = { viewModel.retry() }
            )
        }
    }
}
