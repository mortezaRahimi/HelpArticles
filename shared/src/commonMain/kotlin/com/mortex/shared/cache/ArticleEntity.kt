package com.mortex.shared.cache

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val summary: String,
    val content: String?,          // null if we only have summary
    val updatedAt: Instant,
)