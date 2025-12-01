package com.mortex.helparticles.domain.model

import kotlinx.datetime.Instant


data class ArticleSummary(
    val id: String,
    val title: String,
    val summary: String,
    val updatedAt: Instant
)
