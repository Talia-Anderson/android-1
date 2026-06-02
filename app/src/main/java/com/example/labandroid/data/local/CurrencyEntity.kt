package com.example.labandroid.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class CurrencyEntity(
    @PrimaryKey val id: String,
    val code: String,
    val name: String,
    val rate: Double,
    val isFavorite: Boolean,
    val updatedAt: Long
)
