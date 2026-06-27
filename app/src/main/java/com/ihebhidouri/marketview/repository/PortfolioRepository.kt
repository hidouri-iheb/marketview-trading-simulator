package com.ihebhidouri.marketview.repository

import com.ihebhidouri.marketview.data.room.entity.Portfolio
import com.ihebhidouri.marketview.data.room.entity.Trade
import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    fun getAllPortfolios(userId: String): Flow<List<Portfolio>>
    fun getTradesForPortfolio(portfolioId: Long): Flow<List<Trade>>
    suspend fun getPortfolioById(id: Long): Portfolio?
    suspend fun createPortfolio(portfolio: Portfolio): Long
    suspend fun deletePortfolio(id: Long)
    suspend fun openTrade(trade: Trade)
    suspend fun closeTrade(tradeId: Long, exitPrice: Double)
    suspend fun deleteTrade(id: Long)
    suspend fun addRealizedPnL(portfolioId: Long, pnl: Double)
    fun getAllTrades(): Flow<List<Trade>>

    fun getAllPortfoliosGlobal(): Flow<List<Portfolio>>
}