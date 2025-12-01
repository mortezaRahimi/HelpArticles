package com.mortex.helparticles.domain.model

import kotlinx.datetime.Instant


data class ArticleDetail(
    val id: String,
    val title: String,
    val content: String,
    val updatedAt: Instant
)
