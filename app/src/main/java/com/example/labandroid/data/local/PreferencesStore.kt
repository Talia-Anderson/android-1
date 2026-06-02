package com.example.labandroid.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val gson = Gson()
    private val prefs: SharedPreferences =
        context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)

    fun isFavorite(currencyId: String): Boolean {
        return prefs.getBoolean("favorite_$currencyId", false)
    }

    fun setFavorite(currencyId: String, isFavorite: Boolean) {
        prefs.edit().putBoolean("favorite_$currencyId", isFavorite).apply()
    }

    fun saveCurrencies(currencies: List<CurrencyEntity>) {
        val json = gson.toJson(currencies)
        prefs.edit().putString("saved_currencies", json).apply()
    }

    fun loadCurrencies(): List<CurrencyEntity> {
        val json = prefs.getString("saved_currencies", null) ?: return emptyList()
        val type = object : TypeToken<List<CurrencyEntity>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}
