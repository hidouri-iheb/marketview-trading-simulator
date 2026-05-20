package com.ihebhidouri.marketview.viewmodel



import com.ihebhidouri.marketview.network.StockSearchItemDto

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val results: List<StockSearchItemDto>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}