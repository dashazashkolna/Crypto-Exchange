package com.example.cryptoexchange.domain.model

sealed interface SyncResult {
    data object Success : SyncResult
    data class Failure(val error: SyncError) : SyncResult
}