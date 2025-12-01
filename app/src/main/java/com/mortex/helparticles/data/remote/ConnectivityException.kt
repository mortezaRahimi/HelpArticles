package com.mortex.helparticles.data.remote

/**
 * Simulates connectivity or transport-level errors:
 * - timeout
 * - DNS
 * - 5xx
 * - airplane mode, etc.
 */

class ConnectivityException(
    override val message: String 
) : Exception(message)