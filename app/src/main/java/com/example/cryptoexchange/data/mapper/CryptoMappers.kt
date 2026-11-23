package com.example.cryptoexchange.data.mapper

import com.example.cryptoexchange.data.local.CryptoEntity
import com.example.cryptoexchange.data.remote.CoinDto
import com.example.cryptoexchange.domain.model.Crypto

fun CoinDto.toEntity(updatedAt: Long): CryptoEntity? {
    val safeId = id?.trim().orEmpty()
    val safeName = name?.trim().orEmpty()
    val safeSymbol = symbol?.trim().orEmpty()
    if (safeId.isEmpty() || safeName.isEmpty() || safeSymbol.isEmpty()) return null
    return CryptoEntity(
        id = safeId,
        name = safeName,
        symbol = safeSymbol.uppercase(),
        imageUrl = image?.trim()?.takeIf { it.isNotEmpty() },
        priceUsd = currentPrice?.takeIf { it.isFinite() && it >= 0.0 },
        position = 0,
        updatedAt = updatedAt
    )
}

fun CryptoEntity.toDomain(): Crypto = Crypto(
    id = id,
    name = name,
    symbol = symbol,
    imageUrl = imageUrl,
    priceUsd = priceUsd,
    position = position,
    updatedAt = updatedAt
)