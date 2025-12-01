package com.mortex.helparticles.presentation.navigation

sealed class Screen(val route: String) {

    object Articles : Screen("articles")

    object ArticleDetail : Screen("articleDetail/{id}") {
        fun createRoute(id: String): String = "articleDetail/$id"
    }
}