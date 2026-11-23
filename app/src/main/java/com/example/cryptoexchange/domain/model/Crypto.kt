package com.example.cryptoexchange.domain.model

data class Crypto(
    val id: String,
    val name: String,
    val symbol: String,
    val imageUrl: String?,
    val priceUsd: Double?,
    val position: Int,
    val updatedAt: Long
)