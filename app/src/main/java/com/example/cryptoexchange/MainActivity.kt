package com.example.cryptoexchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptoexchange.presentation.CryptoScreen
import com.example.cryptoexchange.presentation.CryptoViewModel
import com.example.cryptoexchange.ui.theme.CryptoExchangeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as CryptoApp).container
        setContent {
            CryptoExchangeTheme {
                val viewModel: CryptoViewModel = viewModel(factory = CryptoViewModel.factory(container))
                CryptoScreen(viewModel)
            }
        }
    }
}