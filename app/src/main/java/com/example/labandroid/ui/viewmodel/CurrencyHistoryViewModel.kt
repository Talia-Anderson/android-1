package com.example.labandroid.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labandroid.data.repository.CurrencyRepository
import com.example.labandroid.domain.model.CurrencyHistoryEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
class CurrencyHistoryViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {
    private val _history = MutableStateFlow<List<CurrencyHistoryEntry>>(emptyList())
    private val _message = MutableStateFlow<String?>(null)
    val history: StateFlow<List<CurrencyHistoryEntry>> = _history.asStateFlow()
    val message: StateFlow<String?> = _message.asStateFlow()

    fun load(currencyId: String) {
        viewModelScope.launch {
            repository.observeHistory(currencyId).collect { list ->
                _history.value = list
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val result = repository.refreshCurrencies()
            _message.value = result.fold(
                onSuccess = { "Исторические данные обновлены" },
                onFailure = { it.message ?: "Ошибка обновления" }
            )
        }
    }

    fun consumeMessage() {
        _message.value = null
    }
}
