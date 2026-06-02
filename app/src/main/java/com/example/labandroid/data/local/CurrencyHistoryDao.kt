package com.example.labandroid.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyHistoryDao {
    @Query("SELECT * FROM currency_history WHERE currencyId = :currencyId ORDER BY date DESC")
    fun observeHistory(currencyId: String): Flow<List<CurrencyHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHistory(history: List<CurrencyHistoryEntity>)

    @Query("DELETE FROM currency_history WHERE currencyId = :currencyId")
    suspend fun clearHistory(currencyId: String)
}
