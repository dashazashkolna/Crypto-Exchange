package com.example.cryptoexchange.domain.usecase

import com.example.cryptoexchange.domain.model.SyncState
import com.example.cryptoexchange.domain.repository.SyncScheduler
import kotlinx.coroutines.flow.Flow

class ObserveSyncStateUseCase(private val scheduler: SyncScheduler) {
    operator fun invoke(): Flow<SyncState> = scheduler.observeSyncState()
}