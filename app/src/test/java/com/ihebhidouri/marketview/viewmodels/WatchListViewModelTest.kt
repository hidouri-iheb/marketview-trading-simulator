package com.ihebhidouri.marketview.viewmodels

import app.cash.turbine.test
import com.ihebhidouri.marketview.MainDispatcherRule
import com.ihebhidouri.marketview.data.local.WatchedStock
import com.ihebhidouri.marketview.models.Stock
import com.ihebhidouri.marketview.repository.StockRepository
import com.ihebhidouri.marketview.repository.WatchlistRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class WatchlistViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun watchedStock_withLiveData_usesLivePrice() = runTest {
        val watchlistRepo = mockk<WatchlistRepository> {
            every { getWatchlist() } returns flowOf(
                listOf(
                    WatchedStock(
                        symbol = "AAPL",
                        name = "Apple Inc.",
                        exchange = "NASDAQ",
                        currency = "USD",
                        basePrice = 150.0
                    )
                )
            )
        }

        val liveStock = Stock(
            symbol = "AAPL",
            name = "Apple Inc.",
            exchange = "NASDAQ",
            currency = "USD",
            price = 200.0,
            change = 4.0,
            changePercent = 2.0,
            open = 196.0,
            high = 201.0,
            low = 195.0,
            previousClose = 196.0,
            volume = 50000000,
            fiftyTwoWeekHigh = 220.0,
            fiftyTwoWeekLow = 160.0
        )

        val stockRepo = mockk<StockRepository> {
            every { getStocks() } returns flowOf(listOf(liveStock))
        }

        val viewModel = WatchlistViewModel(watchlistRepo, stockRepo)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.stocks.size)
            assertEquals(200.0, state.stocks[0].price, 0.001)
            assertEquals(2.0, state.stocks[0].changePercent, 0.001)
            cancelAndIgnoreRemainingEvents()
        }
    }
}