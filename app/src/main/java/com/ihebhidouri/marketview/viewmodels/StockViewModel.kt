package com.ihebhidouri.marketview.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihebhidouri.marketview.models.Stock
import com.ihebhidouri.marketview.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

data class StockUiState(
    val stocks: List<Stock> = emptyList(),
    val trending: List<Stock> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class StockViewModel : ViewModel() {

    private val repository = StockRepository()

    private val _uiState = MutableStateFlow(StockUiState())
    val uiState: StateFlow<StockUiState> = _uiState

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
                }
            } catch (e: Exception) {
                _uiState.value = StockUiState(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
}