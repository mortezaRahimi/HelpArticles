package com.mortex.helparticles.util

sealed class AppResult<out T> {

    /**
     * Standard domain-level result wrapper.
     * All use cases return AppResult so ViewModels can handle success/error uniformly.
     */

    data class Success<T>(val data: T, val fromCache: Boolean, val isOnline: Boolean) :
        AppResult<T>()

    data class Error(val appError: AppError) : AppResult<Nothing>()

    /**
     * Helper to inspect success in a concise way.
     */
    val isSuccess: Boolean
        get() = this is Success<T>

    /**
     * Helper to inspect error in a concise way.
     */
    val isError: Boolean
        get() = this is Error
}