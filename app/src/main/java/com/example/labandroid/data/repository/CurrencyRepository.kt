package com.example.labandroid.data.repository

import com.example.labandroid.data.local.CurrencyEntity
import com.example.labandroid.data.local.CurrencyHistoryDao
import com.example.labandroid.data.local.CurrencyHistoryEntity
import com.example.labandroid.data.local.CurrencyDao
import com.example.labandroid.data.local.PreferencesStore
import com.example.labandroid.data.remote.ExchangeRatesApi
import com.example.labandroid.domain.model.CurrencyHistoryEntry
import com.example.labandroid.domain.model.CurrencyRate
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Retrofit

@Singleton
class CurrencyRepository @Inject constructor(
    private val currencyDao: CurrencyDao,
    private val historyDao: CurrencyHistoryDao,
    private val preferencesStore: PreferencesStore,
    retrofit: Retrofit
) {
    private val api: ExchangeRatesApi by lazy { retrofit.create(ExchangeRatesApi::class.java) }

    suspend fun seedFromSharedPreferences() {
        if (currencyDao.getAll().isNotEmpty()) return
        val saved = preferencesStore.loadCurrencies()
        if (saved.isNotEmpty()) {
            currencyDao.upsertAll(saved)
        }
    }

    fun observeCurrencies(): Flow<List<CurrencyRate>> {
        return currencyDao.observeCurrencies().map { list ->
            list.map {
                CurrencyRate(
                    id = it.id,
                    code = it.code,
                    name = it.name,
                    rate = it.rate,
                    isFavorite = it.isFavorite,
                    updatedAt = it.updatedAt
                )
            }
        }
    }

    fun observeHistory(currencyId: String): Flow<List<CurrencyHistoryEntry>> {
        return historyDao.observeHistory(currencyId).map { list ->
            list.map { CurrencyHistoryEntry(it.currencyId, it.date, it.rate) }
        }
    }

    suspend fun refreshCurrencies(): Result<Int> {
        val response = runCatching { api.getExchangeRates() }.getOrElse { error ->
            return Result.failure(
                IllegalStateException("Не удалось подключиться к серверу: ${error.message}", error)
            )
        }

        if (!response.isSuccessful) {
            return Result.failure(IllegalStateException("HTTP ошибка: ${response.code()}"))
        }

        val body = response.body()
            ?: return Result.failure(IllegalStateException("Пустой ответ сервера"))

        val currencies = body.currencies.mapNotNull { dto ->
            val code = dto.code?.trim()?.uppercase() ?: return@mapNotNull null
            val rate = dto.resolvedRate() ?: return@mapNotNull null
            val id = dto.id?.ifBlank { null } ?: code
            CurrencyEntity(
                id = id,
                code = code,
                name = dto.name ?: code,
                rate = rate,
                isFavorite = preferencesStore.isFavorite(id),
                updatedAt = parseUpdatedAt(dto.updatedAt)
            )
        }

        if (currencies.isEmpty()) {
            return Result.failure(
                IllegalStateException("Не удалось разобрать валюты из ответа сервера")
            )
        }

        currencyDao.upsertAll(currencies)
        preferencesStore.saveCurrencies(currencies)

        currencies.forEach { currency ->
            val dto = body.currencies.firstOrNull {
                it.id == currency.id || it.code?.trim()?.uppercase() == currency.code
            } ?: return@forEach
            val history = dto.history.orEmpty().mapNotNull { historyDto ->
                val date = historyDto.date ?: return@mapNotNull null
                val historyRate = historyDto.rate ?: return@mapNotNull null
                CurrencyHistoryEntity(currencyId = currency.id, date = date, rate = historyRate)
            }
            if (history.isNotEmpty()) {
                historyDao.clearHistory(currency.id)
                historyDao.upsertHistory(history)
            }
        }

        return Result.success(currencies.size)
    }

    suspend fun toggleFavorite(currencyId: String, isFavorite: Boolean) {
        preferencesStore.setFavorite(currencyId, isFavorite)
        currencyDao.updateFavorite(currencyId, isFavorite)
    }

    private fun parseUpdatedAt(value: String?): Long {
        if (value.isNullOrBlank()) return System.currentTimeMillis()
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(value)?.time
                ?: System.currentTimeMillis()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }
}
