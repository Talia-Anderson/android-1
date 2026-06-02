package com.example.labandroid.data.remote

import com.google.gson.annotations.SerializedName

data class ExchangeRatesResponseDto(
    @SerializedName("currencies")
    val currencies: List<CurrencyDto> = emptyList()
)

data class CurrencyDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("symbol")
    val symbol: String? = null,
    @SerializedName("currentRate")
    val currentRate: Double? = null,
    @SerializedName("rate")
    val rate: Double? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    @SerializedName("history")
    val history: List<HistoryDto>? = null
) {
    fun resolvedRate(): Double? = currentRate ?: rate
}

data class HistoryDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("rate")
    val rate: Double? = null
)
