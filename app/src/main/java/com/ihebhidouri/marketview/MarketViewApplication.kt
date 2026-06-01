package com.ihebhidouri.marketview

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import com.ihebhidouri.marketview.data.local.AppDatabase
import com.ihebhidouri.marketview.repository.FakeStockRepository
import com.ihebhidouri.marketview.repository.StockRepository
import com.ihebhidouri.marketview.repository.WatchlistRepository
import com.ihebhidouri.marketview.data.datastore.ThemePreferencesRepository
import com.ihebhidouri.marketview.repository.WatchlistRepositoryImpl
import com.ihebhidouri.marketview.repository.PortfolioRepositoryImpl
import com.ihebhidouri.marketview.repository.PortfolioRepository
class MarketViewApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val stockRepository: StockRepository = FakeStockRepository(applicationScope)

    val themePreferencesRepository: ThemePreferencesRepository by lazy {
        ThemePreferencesRepository(this)
    }
    val watchlistRepository: WatchlistRepository by lazy {
        val dao = AppDatabase.getDatabase(this).watchlistDao()
        WatchlistRepositoryImpl(dao)
    }
    val portfolioRepository: PortfolioRepository by lazy {
        val db = AppDatabase.getDatabase(this)
        PortfolioRepositoryImpl(db.portfolioDao(), db.tradeDao())
    }
}
