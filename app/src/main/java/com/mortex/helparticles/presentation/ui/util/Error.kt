package com.mortex.helparticles.presentation.ui.util

import androidx.annotation.StringRes
import com.mortex.helparticles.R
import com.mortex.helparticles.util.AppError

private const val HTTP_INTERNAL_ERROR = 500
private const val HTTP_UNAUTHORIZED = 401

@StringRes
fun AppError.toMessageResArticles(): Int = when (this) {
    is AppError.Connectivity -> R.string.no_internet_connection
    is AppError.Backend -> {
        when (this.code) {
            HTTP_INTERNAL_ERROR -> {
                R.string.server_error
            }

            HTTP_UNAUTHORIZED -> {
                R.string.login_issue
            }

            else -> R.string.server_error
        }

    }

    is AppError.Unknown -> R.string.something_went_wrong_please_try_again
}

@StringRes
fun AppError.toMessageResDetails(): Int = when (this) {
    is AppError.Connectivity -> R.string.unable_to_load_article_no_internet
    is AppError.Backend -> {
        R.string.server_error_while_loading_article
    }

    is AppError.Unknown -> R.string.unexpected_error_while_loading_article
}
