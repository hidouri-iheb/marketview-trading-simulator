package com.ihebhidouri.marketview.viewmodels

import app.cash.turbine.test
import com.ihebhidouri.marketview.MainDispatcherRule
import com.ihebhidouri.marketview.models.Stock
import com.ihebhidouri.marketview.repository.StockRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class StockViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun stock(
        symbol: String,
        changePercent: Double
    ) = Stock(
        symbol = symbol,
        name = "$symbol Company",
        exchange = "NASDAQ",
        currency = "USD",
        price = 100.0,
        change = 1.0,
        changePercent = changePercent,
        open = 99.0,
        high = 105.0,
        low = 95.0,
        previousClose = 99.0,
        volume = 1000000,
        fiftyTwoWeekHigh = 120.0,
        fiftyTwoWeekLow = 80.0
    )

    @Test
    fun onSearchQueryChange_updatesSearchQuery() {
        val repository = mockk<StockRepository>(relaxed = true) {
            every { getStocks() } returns flowOf(emptyList())
        }
        val viewModel = StockViewModel(repository)

        viewModel.onSearchQueryChange("AAPL")

        assertEquals("AAPL", viewModel.searchQuery.value)
    }


    @Test
    fun loadStocks_updatesStocksAndTrendingList() = runTest {
        val stocks = listOf(
            stock("AAPL", 1.2),
            stock("TSLA", -3.5),
            stock("MSFT", 0.8)
        )

        val repository = mockk<StockRepository>(relaxed = true) {
            every { getStocks() } returns flowOf(stocks)
        }

        val viewModel = StockViewModel(repository)

        viewModel.uiState.test {
            val state = awaitItem()

            assertEquals(stocks, state.stocks)
            assertEquals("TSLA", state.trending.first().symbol)
            assertEquals(false, state.isLoading)
            assertNull(state.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onStockSelected_updatesSelectedStockAndClearsSearch() = runTest {
        val selectedStock = stock("AAPL", 1.2)

        val repository = mockk<StockRepository>(relaxed = true) {
            every { getStocks() } returns flowOf(emptyList())
            coEvery { getStockDetail("AAPL") } returns selectedStock
        }

        val viewModel = StockViewModel(repository)

        viewModel.onSearchQueryChange("AAPL")
        viewModel.onStockSelected("AAPL")

        assertEquals("", viewModel.searchQuery.value)
        assertEquals(emptyList<Any>(), viewModel.searchResults.value)
        assertEquals(selectedStock, viewModel.selectedStock.value)
        assertEquals(false, viewModel.isCardLoading.value)
    }

    @Test
    fun onDismissCard_clearsSelectedStock() = runTest {
        val selectedStock = stock("AAPL", 1.2)

        val repository = mockk<StockRepository>(relaxed = true) {
            every { getStocks() } returns flowOf(emptyList())
            coEvery { getStockDetail("AAPL") } returns selectedStock
        }

        val viewModel = StockViewModel(repository)

        viewModel.onStockSelected("AAPL")
        viewModel.onDismissCard()

        assertNull(viewModel.selectedStock.value)
    }
}