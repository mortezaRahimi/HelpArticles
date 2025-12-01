package com.mortex.helparticles.data.mapper

import com.mortex.helparticles.data.model.ArticleDetailDto
import com.mortex.helparticles.data.model.ArticleSummaryDto
import com.mortex.helparticles.domain.model.ArticleDetail
import com.mortex.helparticles.domain.model.ArticleSummary

/**
 * Maps DTOs (data layer) to domain models (domain layer).
 */
object ArticleMapper {

    fun toDomainSummary(dto: ArticleSummaryDto): ArticleSummary =
        ArticleSummary(
            id = dto.id,
            title = dto.title,
            summary = dto.summary,
            updatedAt = dto.updatedAt
        )

    fun toDomainDetail(dto: ArticleDetailDto): ArticleDetail =
        ArticleDetail(
            id = dto.id,
            title = dto.title,
            content = dto.content,
            updatedAt = dto.updatedAt
        )
}