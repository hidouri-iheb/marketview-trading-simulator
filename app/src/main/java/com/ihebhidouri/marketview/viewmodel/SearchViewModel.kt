package com.ihebhidouri.marketview.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihebhidouri.marketview.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val repository = StockRepository()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState

    fun searchStocks(query: String, apiKey: String) {
        if (query.isBlank()) {
            _uiState.value = SearchUiState.Error("Please enter a stock name or symbol")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = SearchUiState.Loading

                val results = repository.searchStocks(
                    query = query.trim(),
                    apiKey = apiKey
                )

                _uiState.value = SearchUiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error(
                    e.message ?: "Unknown API error"
                )
            }
        }
    }
}