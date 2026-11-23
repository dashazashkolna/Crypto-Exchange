package com.example.cryptoexchange.domain.usecase

import com.example.cryptoexchange.domain.model.Crypto
import com.example.cryptoexchange.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class ObserveCryptosUseCase(private val repository: CryptoRepository) {
    operator fun invoke(): Flow<List<Crypto>> = repository.observeCryptos()
}