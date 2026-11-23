package com.example.cryptoexchange.domain.usecase

import com.example.cryptoexchange.domain.repository.SyncScheduler

class RequestRefreshUseCase(private val scheduler: SyncScheduler) {
    operator fun invoke() = scheduler.requestRefresh()
}