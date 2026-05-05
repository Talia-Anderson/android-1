package com.example.labandroid.ui.viewmodel

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labandroid.model.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

class CurrencyViewModel : ViewModel() {

    private val _currencies = MutableStateFlow<List<Currency>>(emptyList())
    val currencies: StateFlow<List<Currency>> = _currencies

    private var nextId = 1

    fun addCurrency(code: String, icon: ImageVector) {
        val randomRate = Random.nextDouble(50.0, 150.0)
        val newCurrency = Currency(
            id = nextId++,
            code = code.uppercase(),
            rate = randomRate,
            icon = icon
        )
        _currencies.value = _currencies.value + newCurrency
    }

    fun updateCurrency(currency: Currency, newCode: String, newIcon: ImageVector) {
        val updatedCurrency = currency.copy(
            code = newCode.uppercase(),
            icon = newIcon
        )
        _currencies.value = _currencies.value.map {
            if (it.id == currency.id) updatedCurrency else it
        }
    }

    fun deleteCurrency(currency: Currency) {
        _currencies.value = _currencies.value.filter { it.id != currency.id }
    }

    fun updateRandomRate(currency: Currency) {
        val updatedCurrency = currency.copy(rate = Random.nextDouble(50.0, 150.0))
        _currencies.value = _currencies.value.map {
            if (it.id == currency.id) updatedCurrency else it
        }
    }
}