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

class MarketViewApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val stockRepository: StockRepository = FakeStockRepository(applicationScope)

    val themePreferencesRepository: ThemePreferencesRepository by lazy {
        ThemePreferencesRepository(this)
    }
    val watchlistRepository: WatchlistRepository by lazy {
        val dao = AppDatabase.getDatabase(this).watchlistDao()
        WatchlistRepository(dao)
    }
}