package com.example.cryptoexchange.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cryptoexchange.AppContainer
import com.example.cryptoexchange.domain.usecase.ObserveCryptosUseCase
import com.example.cryptoexchange.domain.usecase.ObserveSyncStateUseCase
import com.example.cryptoexchange.domain.usecase.RequestRefreshUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class CryptoViewModel(
    observeCryptos: ObserveCryptosUseCase,
    observeSyncState: ObserveSyncStateUseCase,
    private val requestRefresh: RequestRefreshUseCase
) : ViewModel() {

    val uiState: StateFlow<CryptoUiState> = combine(
        observeCryptos(),
        observeSyncState()
    ) { cryptos, syncState ->
        CryptoUiState(
            isLoaded = true,
            cryptos = cryptos,
            lastUpdatedAt = cryptos.maxOfOrNull { it.updatedAt },
            syncState = syncState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CryptoUiState()
    )

    fun onRefreshClick() {
        requestRefresh()
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CryptoViewModel(
                    observeCryptos = container.observeCryptosUseCase,
                    observeSyncState = container.observeSyncStateUseCase,
                    requestRefresh = container.requestRefreshUseCase
                )
            }
        }
    }
}