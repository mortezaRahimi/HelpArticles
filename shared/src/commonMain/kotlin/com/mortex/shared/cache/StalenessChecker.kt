package com.mortex.shared.cache

import com.mortex.shared.util.TimeProvider
/**
 * Checks whether cached items are still valid based on TTL.
 */
class StalenessChecker(
    private val ttlMillis: Long,
    private val timeProvider: TimeProvider
) {

    fun  isFresh(item: ArticleEntity?): Boolean {
        if (item == null) return false

        val now = timeProvider.now()
        val age = now.toEpochMilliseconds() - item.updatedAt.toEpochMilliseconds()
        return age <= ttlMillis
    }
}
//check what time is saved in db whe writing