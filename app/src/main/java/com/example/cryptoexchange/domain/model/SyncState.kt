package com.example.cryptoexchange.domain.model

sealed interface SyncState {
    data object Idle : SyncState
    data object Waiting : SyncState
    data object Running : SyncState
    data object Retrying : SyncState
    data class Failed(val error: SyncError) : SyncState
}