package com.example.labandroid.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labandroid.data.repository.CurrencyRepository
import com.example.labandroid.domain.model.CurrencyRate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortMode { CODE, RATE, DATE }

@HiltViewModel
class CurrencyListViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {
    private val filterQuery = MutableStateFlow("")
    private val sortMode = MutableStateFlow(SortMode.CODE)
    private val _isRefreshing = MutableStateFlow(false)
    private val _message = MutableStateFlow<String?>(null)
    private var autoRefreshJob: Job? = null

    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    val message: StateFlow<String?> = _message.asStateFlow()

    val currencies: StateFlow<List<CurrencyRate>> = combine(
        repository.observeCurrencies(),
        filterQuery,
        sortMode
    ) { list, filter, sort ->
        val filtered = if (filter.isBlank()) {
            list
        } else {
            list.filter { it.code.contains(filter, true) || it.name.contains(filter, true) }
        }
        val sorted = when (sort) {
            SortMode.CODE -> filtered.sortedBy { it.code }
            SortMode.RATE -> filtered.sortedByDescending { it.rate }
            SortMode.DATE -> filtered.sortedByDescending { it.updatedAt }
        }
        sorted.sortedByDescending { it.isFavorite }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedFromSharedPreferences()
            refresh()
            startAutoRefresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val result = repository.refreshCurrencies()
            _isRefreshing.value = false
            _message.value = result.fold(
                onSuccess = { count -> "Курсы обновлены ($count)" },
                onFailure = { it.message ?: "Ошибка обновления" }
            )
        }
    }

    fun setFilter(query: String) {
        filterQuery.value = query
    }

    fun setSort(mode: SortMode) {
        sortMode.value = mode
    }

    fun toggleFavorite(item: CurrencyRate) {
        viewModelScope.launch {
            repository.toggleFavorite(item.id, !item.isFavorite)
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (true) {
                delay(60_000)
                repository.refreshCurrencies()
            }
        }
    }
}
