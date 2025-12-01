package com.mortex.shared.cache

import com.mortex.shared.util.TimeProvider

/**
 * Domain-friendly cache abstraction for articles.
 */
@Suppress("UNCHECKED_CAST")
class ArticleCache(
    private val cache: KmpCache,
    private val ttlMillis: Long,
    private val timeProvider: TimeProvider,
) {
    private val stalenessChecker = StalenessChecker(ttlMillis, timeProvider)

    fun <T> saveList(list: List<T>) {
        cache.saveArticleList(list as List<Any>, timeProvider.now())
    }

    fun <T> getFreshList(): List<T>? {
        val item = cache.getArticleList()
        return if (stalenessChecker.isFresh(item)) {
            item!!.value as List<T>
        } else null
    }

    fun <T> saveDetail(id: String, detail: T) {
        cache.saveArticleDetail(id, detail as Any, timeProvider.now())
    }

    fun <T> getFreshDetail(id: String): T? {
        val item = cache.getArticleDetail(id)
        return if (stalenessChecker.isFresh(item)) {
            item!!.value as T
        } else null
    }
}