package com.ihebhidouri.marketview.repository


import com.ihebhidouri.marketview.network.RetrofitInstance
import com.ihebhidouri.marketview.network.StockSearchItemDto

class StockRepository {

    suspend fun searchStocks(
        query: String,
        apiKey: String
    ): List<StockSearchItemDto> {
        val response = RetrofitInstance.api.searchStocks(
            query = query,
            apiKey = apiKey
        )

        return response.data
    }
}
