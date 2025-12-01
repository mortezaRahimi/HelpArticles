package com.mortex.shared.cache

import com.mortex.shared.util.TimeProvider
/**
 * Checks whether cached items are still valid based on TTL.
 */
class StalenessChecker(
    private val ttlMillis: Long,
    private val timeProvider: TimeProvider
) {

    fun <T> isFresh(item: CacheItem<T>?): Boolean {
        if (item == null) return false

        val now = timeProvider.now()
        val age = now.toEpochMilliseconds() - item.timestamp.toEpochMilliseconds()
        return age <= ttlMillis
    }
}