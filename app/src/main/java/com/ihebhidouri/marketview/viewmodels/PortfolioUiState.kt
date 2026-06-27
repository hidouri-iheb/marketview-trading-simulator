package com.ihebhidouri.marketview.viewmodels

import com.ihebhidouri.marketview.data.room.entity.Portfolio
import com.ihebhidouri.marketview.data.room.entity.Trade
import com.ihebhidouri.marketview.models.PortfolioSummary

data class TradeWithPnL(
    val trade: Trade,
    val currentPrice: Double,
    val pnl: Double
)


data class PortfolioListUiState(
    val portfolios: List<PortfolioSummary> = emptyList()
)

data class PortfolioDetailUiState(
    val portfolio: Portfolio? = null,
    val trades: List<TradeWithPnL> = emptyList(),
    val totalPnL: Double = 0.0,
    val currentBalance: Double = 0.0
)

data class TradeHistoryItem(
    val trade: Trade,
    val portfolioName: String,
    val pnl: Double
)
