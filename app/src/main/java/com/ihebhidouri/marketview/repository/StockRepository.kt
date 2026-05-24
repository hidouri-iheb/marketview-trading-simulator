package com.ihebhidouri.marketview.repository

import com.ihebhidouri.marketview.models.Stock
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    fun getStocks(): Flow<List<Stock>>
    suspend fun getStockDetail(symbol: String): Stock?
}