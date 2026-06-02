package com.example.labandroid.domain.model

data class CurrencyRate(
    val id: String,
    val code: String,
    val name: String,
    val rate: Double,
    val isFavorite: Boolean,
    val updatedAt: Long
)
