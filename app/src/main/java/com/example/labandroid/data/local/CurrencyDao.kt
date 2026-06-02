package com.example.labandroid.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currencies ORDER BY isFavorite DESC, code ASC")
    fun observeCurrencies(): Flow<List<CurrencyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(currencies: List<CurrencyEntity>)

    @Query("SELECT * FROM currencies")
    suspend fun getAll(): List<CurrencyEntity>

    @Query("UPDATE currencies SET isFavorite = :isFavorite WHERE id = :currencyId")
    suspend fun updateFavorite(currencyId: String, isFavorite: Boolean)

    @Query("SELECT * FROM currencies WHERE id = :currencyId LIMIT 1")
    suspend fun getCurrencyById(currencyId: String): CurrencyEntity?
}
