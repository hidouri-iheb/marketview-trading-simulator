package com.ihebhidouri.marketview.repository

import com.ihebhidouri.marketview.data.room.entity.Portfolio
import com.ihebhidouri.marketview.data.room.dao.PortfolioDao
import com.ihebhidouri.marketview.data.room.entity.Trade
import com.ihebhidouri.marketview.data.room.dao.TradeDao
import kotlinx.coroutines.flow.Flow

class PortfolioRepositoryImpl(
    private val portfolioDao: PortfolioDao,
    private val tradeDao: TradeDao
) : PortfolioRepository {

    override fun getAllPortfolios(userId: String): Flow<List<Portfolio>> = portfolioDao.getAll(userId)

    override fun getTradesForPortfolio(portfolioId: Long): Flow<List<Trade>> =
        tradeDao.getTradesForPortfolio(portfolioId)

    override suspend fun getPortfolioById(id: Long): Portfolio? = portfolioDao.getById(id)

    override suspend fun createPortfolio(portfolio: Portfolio): Long = portfolioDao.insert(portfolio)

    override suspend fun deletePortfolio(id: Long) = portfolioDao.deleteById(id)

    override suspend fun openTrade(trade: Trade) = tradeDao.insert(trade)

    override suspend fun closeTrade(tradeId: Long, exitPrice: Double) =
        tradeDao.closeTrade(tradeId, exitPrice)

    override suspend fun deleteTrade(id: Long) = tradeDao.deleteById(id)

    override fun getAllTrades(): Flow<List<Trade>> = tradeDao.getAll()

    override suspend fun addRealizedPnL(portfolioId: Long, pnl: Double) =
        portfolioDao.addRealizedPnL(portfolioId, pnl)

    override fun getAllPortfoliosGlobal(): Flow<List<Portfolio>> = portfolioDao.getAllGlobal()
}