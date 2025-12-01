package com.mortex.shared.cache

import kotlinx.datetime.Instant


/**
 * Generic cache container storing a value and the time it was written.
 */
data class CacheItem<T>(
    val value: T,
    val timestamp: Instant
)