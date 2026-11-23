package com.example.cryptoexchange.data.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.cryptoexchange.domain.model.SyncError
import com.example.cryptoexchange.domain.model.SyncState
import com.example.cryptoexchange.domain.repository.SyncScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.concurrent.TimeUnit

class WorkManagerSyncScheduler(context: Context) : SyncScheduler {

    private val workManager = WorkManager.getInstance(context)

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    override fun ensurePeriodicSync() {
        workManager.pruneWork()
        val request = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    override fun requestRefresh() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniqueWork(
            MANUAL_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    override fun observeSyncState(): Flow<SyncState> = combine(
        workManager.getWorkInfosForUniqueWorkFlow(MANUAL_WORK_NAME),
        workManager.getWorkInfosForUniqueWorkFlow(PERIODIC_WORK_NAME)
    ) { manual, periodic -> mapState(manual, periodic) }

    private fun mapState(manual: List<WorkInfo>, periodic: List<WorkInfo>): SyncState {
        val all = manual + periodic
        if (all.any { it.state == WorkInfo.State.RUNNING }) return SyncState.Running
        if (all.any { it.state == WorkInfo.State.ENQUEUED && it.runAttemptCount > 0 }) {
            return SyncState.Retrying
        }
        val failed = manual.firstOrNull { it.state == WorkInfo.State.FAILED }
        if (failed != null) {
            val error = failed.outputData.getString(SyncWorker.KEY_ERROR)
                ?.let { name -> SyncError.entries.firstOrNull { it.name == name } }
                ?: SyncError.SERVER
            return SyncState.Failed(error)
        }
        if (manual.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.BLOCKED }) {
            return SyncState.Waiting
        }
        return SyncState.Idle
    }

    private companion object {
        const val PERIODIC_WORK_NAME = "crypto_periodic_sync"
        const val MANUAL_WORK_NAME = "crypto_manual_sync"
    }
}