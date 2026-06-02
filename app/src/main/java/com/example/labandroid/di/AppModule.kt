package com.example.labandroid.di

import android.content.Context
import androidx.room.Room
import com.example.labandroid.data.local.AppDatabase
import com.example.labandroid.data.local.CurrencyDao
import com.example.labandroid.data.local.CurrencyHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "rates.db").build()
    }

    @Provides
    fun provideCurrencyDao(database: AppDatabase): CurrencyDao = database.currencyDao()

    @Provides
    fun provideHistoryDao(database: AppDatabase): CurrencyHistoryDao = database.historyDao()
}
