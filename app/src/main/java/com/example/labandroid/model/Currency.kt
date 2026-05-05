package com.example.labandroid.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Currency(
    val id: Int = 0,
    val code: String,
    val rate: Double,
    val icon: ImageVector
)