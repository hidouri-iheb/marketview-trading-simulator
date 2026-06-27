package com.ihebhidouri.marketview.viewmodels

import com.ihebhidouri.marketview.models.Stock

data class StockUiState(
    val stocks: List<Stock> = emptyList(),
    val trending: List<Stock> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)