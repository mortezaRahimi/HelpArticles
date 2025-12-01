package com.mortex.shared

import com.mortex.shared.cache.ArticleCache
import com.mortex.shared.cache.KmpCache
import com.mortex.shared.util.TimeProvider
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FakeTimeProvider(var current: Instant) : TimeProvider {
    override fun now(): Instant = current
}

class CacheTest {

    @Test
    fun returnCachedListWhenRefresh() {
        // Arrange
        val initialTime = Instant.fromEpochMilliseconds(0L)
        val fakeClock = FakeTimeProvider(initialTime)
        val cache = ArticleCache(KmpCache(), ttlMillis = 5000, timeProvider = fakeClock)

        val list = listOf("A", "B", "C")
        cache.saveList(list)

        // Act: within TTL
        fakeClock.current = Instant.fromEpochMilliseconds(3000)

        // Assert
        val result = cache.getFreshList<String>()
        assertNotNull(result)
        assertEquals(3, result.size)
    }

    @Test
    fun returnNullWhenListIsStale() {
        // Arrange
        val initialTime = Instant.fromEpochMilliseconds(0L)
        val fakeClock = FakeTimeProvider(initialTime)
        val cache = ArticleCache(KmpCache(), ttlMillis = 5000, timeProvider = fakeClock)

        val list = listOf("A")
        cache.saveList(list)

        // Act: after TTL
        fakeClock.current = Instant.fromEpochMilliseconds(6000)

        // Assert
        val result = cache.getFreshList<String>()
        assertNull(result)
    }
}
