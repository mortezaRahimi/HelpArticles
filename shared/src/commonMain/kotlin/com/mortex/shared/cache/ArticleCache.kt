package com.mortex.shared.cache

import com.mortex.shared.util.TimeProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Domain-friendly cache abstraction for articles.
 */
@Suppress("UNCHECKED_CAST")
class ArticleCache(
    private val cache: ArticleDB,
    private val ttlMillis: Long,
    private val timeProvider: TimeProvider,
    private val mutex: Mutex = Mutex(),
) {
    private val stalenessChecker = StalenessChecker(ttlMillis, timeProvider)

    suspend fun saveList(list: List<ArticleEntity>) {
        mutex.withLock {
            cache.articleDao().saveArticles(list)
        }
    }

    suspend fun getLastCache(): List<ArticleEntity>? {
        mutex.withLock {
            return cache.articleDao().getArticles()
        }
    }

    suspend fun getFreshList(): List<ArticleEntity>? {
        mutex.withLock {
            val items = cache.articleDao().getArticles()
            if (items == null) return null
            val firstItem = items.first()
            return if (stalenessChecker.isFresh(firstItem)) {
                items
            } else null
        }
    }

    suspend fun saveDetail(detail: ArticleEntity) {
        mutex.withLock {
            cache.articleDao().saveDetail(detail)
        }

    }

    suspend fun getFreshDetail(id: String): ArticleEntity? {
        mutex.withLock {
            val item = cache.articleDao().getArticleDetail(id)
            return if (stalenessChecker.isFresh(item)) {
                item
            } else null
        }
    }
}