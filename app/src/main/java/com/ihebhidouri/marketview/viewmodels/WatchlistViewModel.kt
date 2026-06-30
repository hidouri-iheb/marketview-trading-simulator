package com.ihebhidouri.marketview.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihebhidouri.marketview.data.room.entity.WatchedStock
import com.ihebhidouri.marketview.repository.StockRepository
import com.ihebhidouri.marketview.repository.WatchlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import com.ihebhidouri.marketview.models.Stock
import com.ihebhidouri.marketview.repository.AuthRepository

data class WatchlistUiState(
    val stocks: List<Stock> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
class WatchlistViewModel(
    private val watchlistRepository: WatchlistRepository,
    private val stockRepository: StockRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val userId: String get() = authRepository.currentUser?.uid ?: ""


    private val _uiState = MutableStateFlow(WatchlistUiState())

    val uiState: StateFlow<WatchlistUiState> = _uiState

    companion object {
        val DEFAULT_SYMBOLS = setOf("AAPL", "MSFT", "GOOGL")
    }

    init {
        viewModelScope.launch {
            combine(
                watchlistRepository.getWatchlist(userId),
                stockRepository.getStocks()
            ) { savedStocks, liveStocks ->
                val liveMap = liveStocks.associateBy { it.symbol }
                val savedSymbols = savedStocks.map { it.symbol }.toSet()

                val userStocks = savedStocks.mapNotNull { saved ->
                    liveMap[saved.symbol] ?: Stock(
                        symbol = saved.symbol,
                        name = saved.name,
                        exchange = saved.exchange,
                        currency = saved.currency,
                        price = saved.basePrice,
                        change = 0.0,
                        changePercent = 0.0,
                        open = saved.basePrice,
                        high = saved.basePrice,
                        low = saved.basePrice,
                        previousClose = saved.basePrice,
                        volume = 0L,
                        fiftyTwoWeekHigh = 0.0,
                        fiftyTwoWeekLow = 0.0
                    )
                }

                val defaults = DEFAULT_SYMBOLS
                    .filter { it !in savedSymbols }
                    .mapNotNull { symbol -> liveMap[symbol] }

                userStocks + defaults
            }.collect { merged ->
                _uiState.value = WatchlistUiState(stocks = merged)
            }
        }
    }

    fun removeStock(symbol: String) {
        viewModelScope.launch {
            watchlistRepository.removeBySymbol(symbol, userId)
        }
    }
    fun addStockFromMarket(stock: Stock) {
        viewModelScope.launch {
            watchlistRepository.addStock(
                WatchedStock(
                    symbol = stock.symbol,
                    userId = userId,
                    name = stock.name,
                    exchange = stock.exchange,
                    currency = stock.currency,
                    basePrice = stock.price
                )
            )
        }
    }
}
