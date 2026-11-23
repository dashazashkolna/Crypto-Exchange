package com.example.cryptoexchange

import android.content.Context
import androidx.room.Room
import com.example.cryptoexchange.data.local.AppDatabase
import com.example.cryptoexchange.data.remote.ApiKeyInterceptor
import com.example.cryptoexchange.data.remote.CoinGeckoApi
import com.example.cryptoexchange.data.repository.CryptoRepositoryImpl
import com.example.cryptoexchange.data.work.WorkManagerSyncScheduler
import com.example.cryptoexchange.domain.repository.CryptoRepository
import com.example.cryptoexchange.domain.repository.SyncScheduler
import com.example.cryptoexchange.domain.usecase.ObserveCryptosUseCase
import com.example.cryptoexchange.domain.usecase.ObserveSyncStateUseCase
import com.example.cryptoexchange.domain.usecase.RequestRefreshUseCase
import com.example.cryptoexchange.domain.usecase.SyncCryptosUseCase
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val database: AppDatabase = Room
        .databaseBuilder<AppDatabase>(appContext, AppDatabase::class.java, "crypto.db")
        .build()
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor(BuildConfig.COINGECKO_API_KEY))
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val api: CoinGeckoApi = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/api/v3/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CoinGeckoApi::class.java)

    private val repository: CryptoRepository = CryptoRepositoryImpl(api, database.cryptoDao())

    val syncScheduler: SyncScheduler = WorkManagerSyncScheduler(appContext)

    val observeCryptosUseCase = ObserveCryptosUseCase(repository)
    val observeSyncStateUseCase = ObserveSyncStateUseCase(syncScheduler)
    val requestRefreshUseCase = RequestRefreshUseCase(syncScheduler)
    val syncCryptosUseCase = SyncCryptosUseCase(repository)
}