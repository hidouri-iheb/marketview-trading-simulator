package com.ihebhidouri.marketview.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface TwelveDataApi {

    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): QuoteResponse

    @GET("logo")
    suspend fun getLogo(
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): LogoResponse

    companion object {
        const val BASE_URL = "https://api.twelvedata.com/"
    }
}