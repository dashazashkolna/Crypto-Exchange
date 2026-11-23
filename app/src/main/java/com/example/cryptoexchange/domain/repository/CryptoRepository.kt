package com.example.cryptoexchange.domain.repository

import com.example.cryptoexchange.domain.model.Crypto
import com.example.cryptoexchange.domain.model.SyncResult
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {
    fun observeCryptos(): Flow<List<Crypto>>
    suspend fun sync(): SyncResult
}