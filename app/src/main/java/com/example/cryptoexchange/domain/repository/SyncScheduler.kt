package com.example.cryptoexchange.domain.repository

import com.example.cryptoexchange.domain.model.SyncState
import kotlinx.coroutines.flow.Flow

interface SyncScheduler {
    fun ensurePeriodicSync()
    fun requestRefresh()
    fun observeSyncState(): Flow<SyncState>
}