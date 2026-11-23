package com.example.cryptoexchange.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.cryptoexchange.CryptoApp
import com.example.cryptoexchange.domain.model.SyncError
import com.example.cryptoexchange.domain.model.SyncResult

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val container = (applicationContext as CryptoApp).container
        return when (val result = container.syncCryptosUseCase()) {
            SyncResult.Success -> Result.success()
            is SyncResult.Failure -> {
                val canRetry = result.error != SyncError.AUTH && runAttemptCount < MAX_ATTEMPTS
                if (canRetry) {
                    Result.retry()
                } else {
                    Result.failure(workDataOf(KEY_ERROR to result.error.name))
                }
            }
        }
    }

    companion object {
        const val KEY_ERROR = "error"
        private const val MAX_ATTEMPTS = 5
    }
}