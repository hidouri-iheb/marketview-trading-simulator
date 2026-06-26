package com.ihebhidouri.marketview.viewmodels

import com.ihebhidouri.marketview.models.PnLCalculator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihebhidouri.marketview.data.local.Portfolio
import com.ihebhidouri.marketview.data.local.Trade
import com.ihebhidouri.marketview.repository.PortfolioRepository
import com.ihebhidouri.marketview.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.ihebhidouri.marketview.repository.AuthRepository
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

class PortfolioViewModel(
    private val portfolioRepository: PortfolioRepository,
    private val stockRepository: StockRepository,
    private val authRepository: AuthRepository,
    private val pnlCalculator: PnLCalculator = PnLCalculator()
) : ViewModel() {

    private val userId: String get() = authRepository.currentUser?.uid ?: ""

    private val _listState = MutableStateFlow(PortfolioListUiState())
    val listState: StateFlow<PortfolioListUiState> = _listState

    private val _leaderboardState = MutableStateFlow(PortfolioListUiState())
    val leaderboardState: StateFlow<PortfolioListUiState> = _leaderboardState

    private val _detailState = MutableStateFlow(PortfolioDetailUiState())
    val detailState: StateFlow<PortfolioDetailUiState> = _detailState

    private val _openTradesState = MutableStateFlow<List<TradeWithPnL>>(emptyList())
    val openTradesState: StateFlow<List<TradeWithPnL>> = _openTradesState

    private val _historyState = MutableStateFlow<List<TradeHistoryItem>>(emptyList())
    val historyState: StateFlow<List<TradeHistoryItem>> = _historyState

    init {
        viewModelScope.launch {
            combine(
                portfolioRepository.getAllPortfoliosGlobal(),
                portfolioRepository.getAllTrades(),
                stockRepository.getStocks()
            ) { portfolios, trades, stocks ->
                val livePrices = stocks.associate { it.symbol to it.price }
                pnlCalculator.buildSummaries(portfolios, trades, livePrices)
            }.collect { _leaderboardState.value = PortfolioListUiState(portfolios = it) }
        }

        viewModelScope.launch {
            combine(
                portfolioRepository.getAllPortfolios(userId),
                portfolioRepository.getAllTrades(),
                stockRepository.getStocks()
            ) { portfolios, trades, stocks ->
                val livePrices = stocks.associate { it.symbol to it.price }
                pnlCalculator.buildSummaries(portfolios, trades, livePrices)
            }.collect { _listState.value = PortfolioListUiState(portfolios = it) }
        }

        viewModelScope.launch {
            combine(
                portfolioRepository.getAllPortfolios(userId),
                portfolioRepository.getAllTrades(),
                stockRepository.getStocks()
            ) { portfolios, trades, liveStocks ->
                val userPortfolioIds = portfolios.map { it.id }.toSet()
                val liveMap = liveStocks.associateBy { it.symbol }
                val openTrades = trades.filter { it.isOpen && it.portfolioId in userPortfolioIds }

                openTrades.forEach { trade ->
                    val currentPrice = liveMap[trade.symbol]?.price ?: return@forEach
                    val tpHit = trade.takeProfit?.let { tp ->
                        if (trade.type == "BUY") currentPrice >= tp else currentPrice <= tp
                    } ?: false
                    val slHit = trade.stopLoss?.let { sl ->
                        if (trade.type == "BUY") currentPrice <= sl else currentPrice >= sl
                    } ?: false

                    if (tpHit || slHit) {
                        val closePrice = if (tpHit) trade.takeProfit!! else trade.stopLoss!!
                        val pnl = pnlCalculator.calculate(trade, closePrice)
                        portfolioRepository.closeTrade(trade.id, closePrice)
                        portfolioRepository.addRealizedPnL(trade.portfolioId, pnl)
                    }
                }

                openTrades.filter { trade ->
                    val currentPrice = liveMap[trade.symbol]?.price ?: trade.entryPrice
                    val tpHit = trade.takeProfit?.let { tp ->
                        if (trade.type == "BUY") currentPrice >= tp else currentPrice <= tp
                    } ?: false
                    val slHit = trade.stopLoss?.let { sl ->
                        if (trade.type == "BUY") currentPrice <= sl else currentPrice >= sl
                    } ?: false
                    !tpHit && !slHit
                }.map { trade ->
                    val currentPrice = liveMap[trade.symbol]?.price ?: trade.entryPrice
                    val pnl = pnlCalculator.calculate(trade, currentPrice)
                    TradeWithPnL(trade, currentPrice, pnl)
                }
            }.collect { _openTradesState.value = it }
        }

        viewModelScope.launch {
            combine(
                portfolioRepository.getAllPortfolios(userId),
                portfolioRepository.getAllTrades()
            ) { portfolios, trades ->
                val portfolioMap = portfolios.associate { it.id to it.name }
                val userPortfolioIds = portfolios.map { it.id }.toSet()
                trades.filter { !it.isOpen && it.portfolioId in userPortfolioIds }.map { trade ->
                    val pnl = pnlCalculator.calculate(trade, trade.exitPrice ?: trade.entryPrice)
                    TradeHistoryItem(
                        trade = trade,
                        portfolioName = portfolioMap[trade.portfolioId] ?: "Deleted",
                        pnl = pnl
                    )
                }
            }.collect { _historyState.value = it }
        }
    }

    fun createPortfolio(name: String, style: String, startingBalance: Double) {
        viewModelScope.launch {
            portfolioRepository.createPortfolio(
                Portfolio(
                    userId = userId,
                    ownerName = authRepository.currentUser?.displayName ?: "Unknown",
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

    private var detailJob: kotlinx.coroutines.Job? = null

    fun selectPortfolio(portfolioId: Long) {
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            combine(
                portfolioRepository.getAllPortfolios(userId),
                portfolioRepository.getTradesForPortfolio(portfolioId),
                stockRepository.getStocks()
            ) { portfolios, trades, liveStocks ->
                val portfolio = portfolios.find { it.id == portfolioId }
                    ?: return@combine _detailState.value

                val liveMap = liveStocks.associateBy { it.symbol }
                val tradesWithPnL = trades.map { trade ->
                    val currentPrice = liveMap[trade.symbol]?.price ?: trade.entryPrice
                    val pnl = pnlCalculator.calculate(trade, currentPrice)
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
}