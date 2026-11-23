package com.example.cryptoexchange.presentation

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatPrice(price: Double?): String {
    if (price == null) return "Немає даних"
    if (price > 0.0 && price < 0.00000001) return "<\$0.00000001"
    val decimals = when {
        price >= 1.0 -> 2
        price >= 0.01 -> 4
        price >= 0.0001 -> 6
        else -> 8
    }
    return "\$" + String.format(Locale.getDefault(), "%,.${decimals}f", price)
}

fun formatDateTime(millis: Long): String =
    SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date(millis))