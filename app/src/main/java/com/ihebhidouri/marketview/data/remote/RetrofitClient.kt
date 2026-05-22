package com.ihebhidouri.marketview.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val api: TwelveDataApi by lazy {
        Retrofit.Builder()
            .baseUrl(TwelveDataApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwelveDataApi::class.java)
    }
}