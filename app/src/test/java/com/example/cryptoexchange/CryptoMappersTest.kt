package com.example.cryptoexchange

import com.example.cryptoexchange.data.mapper.toEntity
import com.example.cryptoexchange.data.remote.CoinDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CryptoMappersTest {

    @Test
    fun missingPriceAndImageBecomeNull() {
        val entity = CoinDto("bitcoin", "btc", "Bitcoin", null, null).toEntity(1L)
        assertNull(entity?.priceUsd)
        assertNull(entity?.imageUrl)
        assertEquals("BTC", entity?.symbol)
    }

    @Test
    fun smallPriceIsKept() {
        val entity = CoinDto("x", "x", "X", "https://a/b.png", 0.00000123).toEntity(1L)
        assertEquals(0.00000123, entity?.priceUsd ?: -1.0, 0.0)
    }

    @Test
    fun dtoWithoutIdIsRejected() {
        assertNull(CoinDto(null, "btc", "Bitcoin", null, 1.0).toEntity(1L))
    }
}