package com.ihebhidouri.marketview.repository

import com.ihebhidouri.marketview.data.local.WatchedStock
import com.ihebhidouri.marketview.data.local.WatchlistDao
import kotlinx.coroutines.flow.Flow

class WatchlistRepository(private val dao: WatchlistDao) {

    fun getWatchlist(): Flow<List<WatchedStock>> = dao.getAll()

    suspend fun removeStock(stock: WatchedStock) = dao.delete(stock)
    suspend fun addStock(stock: WatchedStock) = dao.insert(stock)

    suspend fun removeBySymbol(symbol: String) = dao.deleteBySymbol(symbol)
}