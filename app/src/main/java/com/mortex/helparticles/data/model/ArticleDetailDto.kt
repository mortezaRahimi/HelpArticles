package com.mortex.helparticles.data.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDetailDto(
    val id: String,
    val title: String,
    val content: String,
    val updatedAt: Instant
)
