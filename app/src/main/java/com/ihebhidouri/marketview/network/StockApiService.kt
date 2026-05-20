package com.ihebhidouri.marketview.network

import retrofit2.http.GET
import retrofit2.http.Query

interface StockApiService {

    @GET("symbol_search")
    suspend fun searchStocks(
        @Query("symbol") query: String,
        @Query("apikey") apiKey: String
    ): StockSearchResponseDto
}