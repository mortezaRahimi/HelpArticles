package com.mortex.helparticles.util

import com.mortex.helparticles.data.remote.BackendException
import com.mortex.helparticles.data.remote.ConnectivityException


/**
 * Maps low-level exceptions to domain-level AppError.
 */
object NetworkErrorMapper {

    fun toAppError(throwable: Throwable): AppError =
        when (throwable) {
            is ConnectivityException -> AppError.Connectivity
            is BackendException -> AppError.Backend(
                code = throwable.errorCode,
                title = throwable.errorTitle,
                message = throwable.message
            )

            else -> AppError.Unknown(throwable)
        }
}