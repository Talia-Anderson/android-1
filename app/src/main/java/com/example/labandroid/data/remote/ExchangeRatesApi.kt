package com.example.labandroid.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface ExchangeRatesApi {
    @GET("exchange-rates")
    suspend fun getExchangeRates(): Response<ExchangeRatesResponseDto>
}
