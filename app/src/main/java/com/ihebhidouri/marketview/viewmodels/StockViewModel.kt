package com.ihebhidouri.marketview.viewmodels

import androidx.lifecycle.viewModelScope
import com.ihebhidouri.marketview.models.Stock

import com.ihebhidouri.marketview.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import com.ihebhidouri.marketview.data.SearchableStock
import com.ihebhidouri.marketview.data.SearchableStocks
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ihebhidouri.marketview.MarketViewApplication


data class StockUiState(
    val stocks: List<Stock> = emptyList(),
    val trending: List<Stock> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class StockViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StockRepository =
        (application as MarketViewApplication).stockRepository


    private val _uiState = MutableStateFlow(StockUiState())
    val uiState: StateFlow<StockUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<SearchableStock>>(emptyList())
    val searchResults: StateFlow<List<SearchableStock>> = _searchResults

    private val _selectedStock = MutableStateFlow<Stock?>(null)
    val selectedStock: StateFlow<Stock?> = _selectedStock

    private val _isCardLoading = MutableStateFlow(false)
    val isCardLoading: StateFlow<Boolean> = _isCardLoading

    init {
        loadStocks()
    }

    private fun loadStocks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                repository.getStocks().collect { stocks ->
                    val trending = stocks
                        .sortedByDescending { abs(it.changePercent) }
                        .take(8)

                    _uiState.value = StockUiState(
                        stocks = stocks,
                        trending = trending,
                        isLoading = false
                    )
                    _selectedStock.value?.let { selected ->
                        _selectedStock.value = stocks.find { it.symbol == selected.symbol }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = StockUiState(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _searchResults.value = if (query.isBlank()) {
            emptyList()
        } else {
            SearchableStocks.ALL.filter {
                it.symbol.startsWith(query, ignoreCase = true) ||
                        it.name.startsWith(query, ignoreCase = true)
            }
        }.take(4)
    }
    fun onStockSelected(symbol: String) {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _isCardLoading.value = true

        viewModelScope.launch {
            val stock = repository.getStockDetail(symbol)
            _selectedStock.value = stock
            _isCardLoading.value = false
        }
    }

    fun onDismissCard() {
        _selectedStock.value = null
    }
}