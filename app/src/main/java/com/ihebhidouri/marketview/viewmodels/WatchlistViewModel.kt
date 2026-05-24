package com.ihebhidouri.marketview.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ihebhidouri.marketview.data.local.AppDatabase
import com.ihebhidouri.marketview.data.local.WatchedStock
import com.ihebhidouri.marketview.repository.WatchlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.ihebhidouri.marketview.repository.StockRepository
import com.ihebhidouri.marketview.MarketViewApplication

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WatchlistRepository
    private val stockRepository: StockRepository =
        (application as MarketViewApplication).stockRepository

    private val _watchlist = MutableStateFlow<List<WatchedStock>>(emptyList())
    val watchlist: StateFlow<List<WatchedStock>> = _watchlist

    init {
        val dao = AppDatabase.getDatabase(application).watchlistDao()
        repository = WatchlistRepository(dao)

        viewModelScope.launch {
            repository.getWatchlist().collect { stocks ->
                _watchlist.value = stocks
            }
        }
        viewModelScope.launch {
            stockRepository.getStocks().collect { streamedStocks ->
                val savedSymbols = _watchlist.value.map { it.symbol }.toSet()
                val priceMap = streamedStocks.associateBy { it.symbol }

                _watchlist.value = _watchlist.value.map { saved ->
                    val live = priceMap[saved.symbol]
                    if (live != null) saved.copy(basePrice = live.price)
                    else saved
                }
            }
        }
    }

    fun addStock(stock: WatchedStock) {
        viewModelScope.launch {
            repository.addStock(stock)
        }
    }

    fun removeStock(stock: WatchedStock) {
        viewModelScope.launch {
            repository.removeStock(stock)
        }
    }
}