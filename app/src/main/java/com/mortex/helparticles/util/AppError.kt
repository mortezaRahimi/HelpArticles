package com.mortex.helparticles.util

sealed class AppError {

    /**
     * No internet, timeout, DNS, or 5xx transport problems.
     */
    object Connectivity: AppError()

    /**
     * Backend provided a structured error payload.
     */
    data class Backend(
        val code: Int?,
        val title: String?,
        val message: String?,
    ) : AppError()

    /**
     * Something unexpected happened (parsing bug, unknown exception, etc.).
     */
    data class Unknown(val throwable: Throwable? = null) : AppError()


}