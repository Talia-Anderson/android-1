package com.example.labandroid.data.local

import androidx.room.Entity

@Entity(
    tableName = "currency_history",
    primaryKeys = ["currencyId", "date"]
)
data class CurrencyHistoryEntity(
    val currencyId: String,
    val date: String,
    val rate: Double
)
