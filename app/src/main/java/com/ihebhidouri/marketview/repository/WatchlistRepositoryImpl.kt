package com.ihebhidouri.marketview.repository

import com.ihebhidouri.marketview.data.local.WatchedStock
import com.ihebhidouri.marketview.data.local.WatchlistDao
import kotlinx.coroutines.flow.Flow

class WatchlistRepositoryImpl(private val dao: WatchlistDao) : WatchlistRepository {
    override fun getWatchlist(): Flow<List<WatchedStock>> = dao.getAll()
    override suspend fun removeStock(stock: WatchedStock) = dao.delete(stock)
    override suspend fun addStock(stock: WatchedStock) = dao.insert(stock)
    override suspend fun removeBySymbol(symbol: String) = dao.deleteBySymbol(symbol)
}