package com.mortex.helparticles.data.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ArticleSummaryDto(
    val id: String,
    val title: String,
    val summary: String,
    val updatedAt: Instant,
)
