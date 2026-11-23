package com.example.cryptoexchange.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.cryptoexchange.R
import com.example.cryptoexchange.domain.model.Crypto
import com.example.cryptoexchange.domain.model.SyncError
import com.example.cryptoexchange.domain.model.SyncState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoScreen(viewModel: CryptoViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Біржа криптовалют") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            StatusPanel(state = state, onRefresh = viewModel::onRefreshClick)
            HorizontalDivider()
            when {
                !state.isLoaded -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.cryptos.isEmpty() -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Даних ще немає. Дочекайтесь першого завантаження або натисніть «Оновити».",
                        textAlign = TextAlign.Center
                    )
                }
                else -> CryptoList(cryptos = state.cryptos, listState = listState)
            }
        }
    }
}

@Composable
private fun StatusPanel(state: CryptoUiState, onRefresh: () -> Unit) {
    val syncState = state.syncState
    val busy = syncState is SyncState.Running || syncState is SyncState.Waiting
    val isError = syncState is SyncState.Failed || syncState is SyncState.Retrying

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.lastUpdatedAt
                        ?.let { "Останнє оновлення: ${formatDateTime(it)}" }
                        ?: "Останнє оновлення: ще не було",
                    style = MaterialTheme.typography.bodyMedium
                )
                val message = statusMessage(syncState)
                if (message != null) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isError) MaterialTheme.colorScheme.error else Color.Unspecified
                    )
                }
            }
            Button(onClick = onRefresh, enabled = !busy) {
                Text("Оновити")
            }
        }
        if (syncState is SyncState.Running) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}

private fun statusMessage(syncState: SyncState): String? = when (syncState) {
    SyncState.Idle -> null
    SyncState.Waiting -> "Очікування мережі…"
    SyncState.Running -> "Оновлення…"
    SyncState.Retrying -> "Тимчасова помилка, буде повторна спроба"
    is SyncState.Failed -> when (syncState.error) {
        SyncError.AUTH -> "Помилка ключа API або прав доступу"
        SyncError.RATE_LIMIT -> "Перевищено ліміт запитів API"
        SyncError.NETWORK -> "Не вдалося з'єднатися із сервером"
        SyncError.SERVER -> "Помилка сервера"
        SyncError.INVALID_DATA -> "Сервер повернув некоректні дані"
    }
}

@Composable
private fun CryptoList(cryptos: List<Crypto>, listState: LazyListState) {
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        items(items = cryptos, key = { it.id }) { crypto ->
            CryptoItem(crypto)
            HorizontalDivider()
        }
    }
}

@Composable
private fun CryptoItem(crypto: Crypto) {
    val placeholder = painterResource(R.drawable.ic_coin_placeholder)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = crypto.position.toString(),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(28.dp)
        )
        AsyncImage(
            model = crypto.imageUrl,
            contentDescription = crypto.name,
            placeholder = placeholder,
            error = placeholder,
            fallback = placeholder,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = crypto.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = crypto.symbol,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = formatPrice(crypto.priceUsd),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}