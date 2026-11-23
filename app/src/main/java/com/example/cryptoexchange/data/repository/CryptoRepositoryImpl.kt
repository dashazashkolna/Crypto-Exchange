package com.example.cryptoexchange.data.repository

import com.example.cryptoexchange.data.local.CryptoDao
import com.example.cryptoexchange.data.mapper.toDomain
import com.example.cryptoexchange.data.mapper.toEntity
import com.example.cryptoexchange.data.remote.CoinDto
import com.example.cryptoexchange.data.remote.CoinGeckoApi
import com.example.cryptoexchange.domain.model.Crypto
import com.example.cryptoexchange.domain.model.SyncError
import com.example.cryptoexchange.domain.model.SyncResult
import com.example.cryptoexchange.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class CryptoRepositoryImpl(
    private val api: CoinGeckoApi,
    private val dao: CryptoDao
) : CryptoRepository {

    override fun observeCryptos(): Flow<List<Crypto>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun sync(): SyncResult {
        val dtos: List<CoinDto> = try {
            api.getMarkets()
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            return SyncResult.Failure(
                when (e.code()) {
                    401, 403 -> SyncError.AUTH
                    429 -> SyncError.RATE_LIMIT
                    else -> SyncError.SERVER
                }
            )
        } catch (e: IOException) {
            return SyncResult.Failure(SyncError.NETWORK)
        } catch (e: Exception) {
            return SyncResult.Failure(SyncError.INVALID_DATA)
        }

        val now = System.currentTimeMillis()
        val entities = dtos
            .mapNotNull { it.toEntity(now) }
            .distinctBy { it.id }
            .mapIndexed { index, entity -> entity.copy(position = index + 1) }

        if (entities.isEmpty()) return SyncResult.Failure(SyncError.INVALID_DATA)

        dao.replaceAll(entities)
        return SyncResult.Success
    }
}