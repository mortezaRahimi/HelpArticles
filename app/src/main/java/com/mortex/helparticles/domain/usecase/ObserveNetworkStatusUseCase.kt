package com.mortex.helparticles.domain.usecase

import com.mortex.helparticles.data.network.NetworkStatusMonitor
import kotlinx.coroutines.flow.Flow

/**
  *Intended for observing network connectivity changes.
 */
class ObserveNetworkStatusUseCase(private val networkStatusMonitor: NetworkStatusMonitor) {
    operator fun invoke(): Flow<Boolean> = networkStatusMonitor.isOnline
}