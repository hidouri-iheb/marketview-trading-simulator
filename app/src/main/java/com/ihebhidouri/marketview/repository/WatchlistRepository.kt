package com.ihebhidouri.marketview.repository

import com.ihebhidouri.marketview.data.room.entity.WatchedStock
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun getWatchlist(userId: String): Flow<List<WatchedStock>>
    suspend fun removeStock(stock: WatchedStock)
    suspend fun addStock(stock: WatchedStock)
    suspend fun removeBySymbol(symbol: String, userId: String)
}