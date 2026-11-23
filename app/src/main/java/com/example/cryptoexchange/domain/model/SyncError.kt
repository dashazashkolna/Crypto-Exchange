package com.example.cryptoexchange.domain.model

enum class SyncError {
    NETWORK,
    RATE_LIMIT,
    SERVER,
    INVALID_DATA,
    AUTH
}