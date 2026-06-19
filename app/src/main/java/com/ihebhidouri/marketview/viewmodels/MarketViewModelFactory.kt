package com.ihebhidouri.marketview.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ihebhidouri.marketview.data.datastore.ThemePreferencesRepository
import com.ihebhidouri.marketview.repository.StockRepository
import com.ihebhidouri.marketview.repository.WatchlistRepository
import com.ihebhidouri.marketview.repository.PortfolioRepository
import com.ihebhidouri.marketview.repository.AuthRepository

class MarketViewViewModelFactory(
    private val stockRepository: StockRepository,
    private val watchlistRepository: WatchlistRepository,
    private val themeRepository: ThemePreferencesRepository ,
    private val portfolioRepository: PortfolioRepository ,
    private val authRepository: AuthRepository

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StockViewModel::class.java) ->
                StockViewModel(stockRepository) as T

            modelClass.isAssignableFrom(WatchlistViewModel::class.java) ->
                WatchlistViewModel(watchlistRepository, stockRepository) as T

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(themeRepository) as T

            modelClass.isAssignableFrom(PortfolioViewModel::class.java) ->
                PortfolioViewModel(portfolioRepository, stockRepository) as T

            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(authRepository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}