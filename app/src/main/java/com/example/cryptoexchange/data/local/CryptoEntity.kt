package com.example.cryptoexchange.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cryptos")
data class CryptoEntity(
    @PrimaryKey val id: String,
    val name: String,
    val symbol: String,
    val imageUrl: String?,
    val priceUsd: Double?,
    val position: Int,
    val updatedAt: Long
)