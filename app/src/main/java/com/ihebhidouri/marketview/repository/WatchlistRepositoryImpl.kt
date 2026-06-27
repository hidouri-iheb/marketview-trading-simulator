package com.ihebhidouri.marketview.repository

import com.ihebhidouri.marketview.data.room.entity.WatchedStock
import com.ihebhidouri.marketview.data.room.dao.WatchlistDao
import kotlinx.coroutines.flow.Flow

class WatchlistRepositoryImpl(private val dao: WatchlistDao) : WatchlistRepository {
    override fun getWatchlist(userId: String): Flow<List<WatchedStock>> = dao.getAll(userId)
    override suspend fun removeStock(stock: WatchedStock) = dao.delete(stock)
    override suspend fun addStock(stock: WatchedStock) = dao.insert(stock)
    override suspend fun removeBySymbol(symbol: String, userId: String) = dao.deleteBySymbol(symbol, userId)
}