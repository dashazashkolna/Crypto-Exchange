package com.example.cryptoexchange

import android.app.Application

class CryptoApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.syncScheduler.ensurePeriodicSync()
    }
}