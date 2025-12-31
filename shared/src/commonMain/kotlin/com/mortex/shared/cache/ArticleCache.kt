package com.mortex.shared.cache

import com.mortex.shared.util.TimeProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Domain-friendly cache abstraction for articles.
 */
@Suppress("UNCHECKED_CAST")
class ArticleCache(
    private val cache: KmpCache,
    private val ttlMillis: Long,
    private val timeProvider: TimeProvider,
    private val mutex: Mutex = Mutex(),
) {
    private val stalenessChecker = StalenessChecker(ttlMillis, timeProvider)

    suspend fun <T> saveList(list: List<T>) {
        mutex.withLock {
            cache.saveArticleList(list as List<Any>, timeProvider.now())
        }
    }

    suspend fun <T> getFreshList(): List<T>? {
        mutex.withLock {
            val item = cache.getArticleList()
            return if (stalenessChecker.isFresh(item)) {
                item!!.value as List<T>
            } else null
        }
    }

    suspend fun <T> saveDetail(id: String, detail: T) {
        mutex.withLock {
            cache.saveArticleDetail(id, detail as Any, timeProvider.now())
        }

    }

    suspend fun <T> getFreshDetail(id: String): T? {
        mutex.withLock {
            val item = cache.getArticleDetail(id)
            return if (stalenessChecker.isFresh(item)) {
                item!!.value as T
            } else null
        }
    }
}