package com.mortex.helparticles.util

/**
 *  TTL/staleness rules.
 * The shared KMP cache will use the same values.
 */
object CachePolicy {

    /**
     * Time To Live for cached articles in milliseconds.
     *
         @ARTICLE_TTL_MS  = 1 min.
     */
    const val ARTICLE_TTL_MS: Long = 60_000L

}