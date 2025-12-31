package com.mortex.shared.cache

import kotlinx.datetime.Instant

/**
 * A simple KMP-friendly cache for storing help articles.
 *
 * Backed by in-memory Maps.
 */
object KmpCache {

    private var articleList: CacheItem<List<Any>>? = null
    private val articleDetails = mutableMapOf<String, CacheItem<Any>>()

     fun saveArticleList(list: List<Any>, timestamp: Instant) {
        articleList = CacheItem(list, timestamp)
    }


     fun getArticleList(): CacheItem<List<Any>>? {
        return articleList
    }

     fun saveArticleDetail(
        id: String,
        detail: Any,
        timestamp: Instant,
    ) {
        articleDetails[id] = CacheItem(detail, timestamp)
    }

     fun getArticleDetail(id: String): CacheItem<Any>? {
        return articleDetails[id]
    }

}