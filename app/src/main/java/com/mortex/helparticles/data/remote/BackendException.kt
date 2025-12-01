package com.mortex.helparticles.data.remote

import kotlinx.serialization.Serializable

@Serializable
class BackendException(
    val errorCode: Int?,
    val errorTitle: String?,
    override val message: String?
) : RuntimeException(message)


@Serializable
data class BackendErrorDto(
    val errorCode: Int? = null,
    val errorTitle: String? = null,
    val errorMessage: String? = null
)