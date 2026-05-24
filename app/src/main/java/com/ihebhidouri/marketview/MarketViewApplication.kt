package com.ihebhidouri.marketview

import android.app.Application
import com.ihebhidouri.marketview.repository.FakeStockRepository
import com.ihebhidouri.marketview.repository.StockRepository

class MarketViewApplication : Application() {
    val stockRepository: StockRepository = FakeStockRepository()
}