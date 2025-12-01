package com.mortex.shared.util


import kotlinx.datetime.Clock
import kotlinx.datetime.Instant


/**
 * Abstraction for time.
 */
interface TimeProvider {
    fun now(): Instant
}

object DefaultTimeProvider : TimeProvider {
    override fun now(): Instant = Clock.System.now()
}