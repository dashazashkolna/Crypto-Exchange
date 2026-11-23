package com.example.cryptoexchange.presentation

import com.example.cryptoexchange.domain.model.Crypto
import com.example.cryptoexchange.domain.model.SyncState

data class CryptoUiState(
    val isLoaded: Boolean = false,
    val cryptos: List<Crypto> = emptyList(),
    val lastUpdatedAt: Long? = null,
    val syncState: SyncState = SyncState.Idle
)