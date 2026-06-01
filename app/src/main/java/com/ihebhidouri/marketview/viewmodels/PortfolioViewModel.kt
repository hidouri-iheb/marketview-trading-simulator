package com.ihebhidouri.marketview.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihebhidouri.marketview.data.local.Portfolio
import com.ihebhidouri.marketview.data.local.Trade
import com.ihebhidouri.marketview.models.Stock
import com.ihebhidouri.marketview.repository.PortfolioRepository
import com.ihebhidouri.marketview.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class TradeWithPnL(
    val trade: Trade,
    val currentPrice: Double,
    val pnl: Double
)

data class PortfolioSummary(
    val portfolio: Portfolio,
    val pnlPercent: Double ,
    val currentBalance: Double
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

class PortfolioViewModel(
    private val portfolioRepository: PortfolioRepository,
    private val stockRepository: StockRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(PortfolioListUiState())
    val listState: StateFlow<PortfolioListUiState> = _listState

    private val _detailState = MutableStateFlow(PortfolioDetailUiState())
    val detailState: StateFlow<PortfolioDetailUiState> = _detailState

    init {
        viewModelScope.launch {
            combine(
                portfolioRepository.getAllPortfolios(),
                portfolioRepository.getAllTrades(),
                stockRepository.getStocks()
            ) { portfolios, allTrades, liveStocks ->
                val liveMap = liveStocks.associateBy { it.symbol }
                portfolios.map { portfolio ->
                    val trades = allTrades.filter { it.portfolioId == portfolio.id }
                    val unrealizedPnL = trades.filter { it.isOpen }.sumOf { trade ->
                        val currentPrice = liveMap[trade.symbol]?.price ?: trade.entryPrice
                        calculatePnL(trade, currentPrice)
                    }
                    val totalPnL = portfolio.realizedPnL + unrealizedPnL
                    val pnlPercent = if (portfolio.startingBalance > 0) {
                        (totalPnL / portfolio.startingBalance) * 100.0
                    } else 0.0

                    val currentBalance = portfolio.startingBalance + totalPnL

                    PortfolioSummary(portfolio, pnlPercent, currentBalance)
                }
            }.collect { summaries ->
                _listState.value = PortfolioListUiState(portfolios = summaries)
            }
        }
    }

    fun createPortfolio(name: String, style: String, startingBalance: Double) {
        viewModelScope.launch {
            portfolioRepository.createPortfolio(
                Portfolio(
                    name = name,
                    style = style,
                    startingBalance = startingBalance
                )
            )
        }
    }

    fun deletePortfolio(id: Long) {
        viewModelScope.launch {
            portfolioRepository.deletePortfolio(id)
        }
    }

    fun selectPortfolio(portfolioId: Long) {
        viewModelScope.launch {
            val portfolio = portfolioRepository.getPortfolioById(portfolioId) ?: return@launch

            combine(
                portfolioRepository.getTradesForPortfolio(portfolioId),
                stockRepository.getStocks()
            ) { trades, liveStocks ->
                val liveMap = liveStocks.associateBy { it.symbol }
                val tradesWithPnL = trades.map { trade ->
                    val currentPrice = liveMap[trade.symbol]?.price ?: trade.entryPrice
                    val pnl = calculatePnL(trade, currentPrice)
                    TradeWithPnL(trade, currentPrice, pnl)
                }
                val unrealizedPnL = tradesWithPnL.filter { it.trade.isOpen }.sumOf { it.pnl }
                val totalPnL = portfolio.realizedPnL + unrealizedPnL
                val currentBalance = portfolio.startingBalance + totalPnL

                PortfolioDetailUiState(
                    portfolio = portfolio,
                    trades = tradesWithPnL,
                    totalPnL = totalPnL,
                    currentBalance = currentBalance
                )
            }.collect { state ->
                _detailState.value = state
            }
        }
    }

    fun openTrade(
        portfolioId: Long,
        symbol: String,
        name: String,
        type: String,
        size: Double,
        leverage: Double,
        entryPrice: Double,
        takeProfit: Double?,
        stopLoss: Double?
    ) {
        viewModelScope.launch {
            portfolioRepository.openTrade(
                Trade(
                    portfolioId = portfolioId,
                    symbol = symbol,
                    name = name,
                    type = type,
                    size = size,
                    leverage = leverage,
                    entryPrice = entryPrice,
                    takeProfit = takeProfit,
                    stopLoss = stopLoss,
                    exitPrice = null
                )
            )
        }
    }

    fun closeTrade(tradeId: Long, currentPrice: Double) {
        viewModelScope.launch {
            val tradeWithPnL = _detailState.value.trades.find { it.trade.id == tradeId }
            if (tradeWithPnL != null) {
                val pnl = tradeWithPnL.pnl
                val portfolioId = tradeWithPnL.trade.portfolioId
                portfolioRepository.closeTrade(tradeId, currentPrice)
                portfolioRepository.addRealizedPnL(portfolioId, pnl)
            }
        }
    }

    fun deleteTrade(id: Long) {
        viewModelScope.launch {
            portfolioRepository.deleteTrade(id)
        }
    }

    private fun calculatePnL(trade: Trade, currentPrice: Double): Double {
        val price = if (trade.isOpen) currentPrice else (trade.exitPrice ?: trade.entryPrice)
        return when (trade.type) {
            "BUY" -> (price - trade.entryPrice) * trade.size * trade.leverage
            "SELL" -> (trade.entryPrice - price) * trade.size * trade.leverage
            else -> 0.0
        }
    }
}