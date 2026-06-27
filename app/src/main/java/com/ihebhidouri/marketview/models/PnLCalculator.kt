package com.ihebhidouri.marketview.models

import com.ihebhidouri.marketview.data.room.entity.Portfolio
import com.ihebhidouri.marketview.data.room.entity.Trade
import com.ihebhidouri.marketview.models.PortfolioSummary

class PnLCalculator {

    fun calculate(trade: Trade, currentPrice: Double): Double {
        val price = if (trade.isOpen) currentPrice else (trade.exitPrice ?: trade.entryPrice)
        return when (trade.type) {
            "BUY" -> (price - trade.entryPrice) * trade.size * trade.leverage
            "SELL" -> (trade.entryPrice - price) * trade.size * trade.leverage
            else -> 0.0
        }
    }
    fun buildSummaries(
        portfolios: List<Portfolio>,
        allTrades: List<Trade>,
        livePrices: Map<String, Double>
    ): List<PortfolioSummary> {
        return portfolios.map { portfolio ->
            val trades = allTrades.filter { it.portfolioId == portfolio.id }
            val unrealizedPnL = trades.filter { it.isOpen }.sumOf { trade ->
                calculate(trade, livePrices[trade.symbol] ?: trade.entryPrice)
            }
            val totalPnL = portfolio.realizedPnL + unrealizedPnL
            val pnlPercent = if (portfolio.startingBalance > 0) {
                (totalPnL / portfolio.startingBalance) * 100.0
            } else 0.0
            val currentBalance = portfolio.startingBalance + totalPnL
            PortfolioSummary(portfolio, pnlPercent, currentBalance, portfolio.ownerName)
        }
    }
    fun shouldAutoClose(trade: Trade, currentPrice: Double): AutoCloseResult {
        val tpHit = trade.takeProfit?.let { tp ->
            if (trade.type == "BUY") currentPrice >= tp else currentPrice <= tp
        } ?: false
        val slHit = trade.stopLoss?.let { sl ->
            if (trade.type == "BUY") currentPrice <= sl else currentPrice >= sl
        } ?: false

        return when {
            tpHit -> AutoCloseResult(shouldClose = true, closePrice = trade.takeProfit!!, reason = "TP")
            slHit -> AutoCloseResult(shouldClose = true, closePrice = trade.stopLoss!!, reason = "SL")
            else -> AutoCloseResult(shouldClose = false)
        }
    }

    data class AutoCloseResult(
        val shouldClose: Boolean,
        val closePrice: Double = 0.0,
        val reason: String = ""
    )
}
