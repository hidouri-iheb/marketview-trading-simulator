package com.ihebhidouri.marketview.viewmodels

import app.cash.turbine.test
import com.ihebhidouri.marketview.MainDispatcherRule
import com.ihebhidouri.marketview.data.local.Portfolio
import com.ihebhidouri.marketview.data.local.Trade
import com.ihebhidouri.marketview.models.Stock
import com.ihebhidouri.marketview.repository.PortfolioRepository
import com.ihebhidouri.marketview.repository.StockRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PortfolioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val portfolio = Portfolio(
        id = 1L,
        name = "Test",
        style = "Swing",
        startingBalance = 10000.0,
        realizedPnL = 0.0
    )

    private fun liveStock(symbol: String, price: Double) = Stock(
        symbol = symbol,
        name = "$symbol Company",
        exchange = "NASDAQ",
        currency = "USD",
        price = price,
        change = 0.0,
        changePercent = 0.0,
        open = price,
        high = price,
        low = price,
        previousClose = price,
        volume = 1000000,
        fiftyTwoWeekHigh = price + 50,
        fiftyTwoWeekLow = price - 50
    )

    private fun openTrade(
        symbol: String,
        type: String,
        entryPrice: Double,
        size: Double,
        leverage: Double
    ) = Trade(
        id = 1L,
        portfolioId = 1L,
        symbol = symbol,
        name = "$symbol Company",
        type = type,
        size = size,
        leverage = leverage,
        entryPrice = entryPrice,
        takeProfit = null,
        stopLoss = null,
        exitPrice = null,
        isOpen = true
    )

    private fun createViewModel(
        trades: List<Trade> = emptyList(),
        liveStocks: List<Stock> = emptyList()
    ): PortfolioViewModel {
        val portfolioRepo = mockk<PortfolioRepository>(relaxed = true) {
            every { getAllPortfolios() } returns flowOf(listOf(portfolio))
            every { getAllTrades() } returns flowOf(trades)
            every { getTradesForPortfolio(1L) } returns flowOf(trades)
            coEvery { getPortfolioById(1L) } returns portfolio
        }
        val stockRepo = mockk<StockRepository>(relaxed = true) {
            every { getStocks() } returns flowOf(liveStocks)
        }
        return PortfolioViewModel(portfolioRepo, stockRepo)
    }

    @Test
    fun buyTrade_priceGoesUp_positivePnL() = runTest {
        val trade = openTrade("AAPL", "BUY", entryPrice = 100.0, size = 10.0, leverage = 1.0)
        val live = liveStock("AAPL", price = 110.0)
        val viewModel = createViewModel(trades = listOf(trade), liveStocks = listOf(live))

        viewModel.selectPortfolio(1L)

        viewModel.detailState.test {
            val state = awaitItem()
            assertEquals(100.0, state.trades[0].pnl, 0.001)
            assertEquals(10100.0, state.currentBalance, 0.001)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun sellTrade_priceGoesUp_negativePnL() = runTest {
        val trade = openTrade("AAPL", "SELL", entryPrice = 100.0, size = 10.0, leverage = 1.0)
        val live = liveStock("AAPL", price = 110.0)
        val viewModel = createViewModel(trades = listOf(trade), liveStocks = listOf(live))

        viewModel.selectPortfolio(1L)

        viewModel.detailState.test {
            val state = awaitItem()
            assertEquals(-100.0, state.trades[0].pnl, 0.001)
            assertEquals(9900.0, state.currentBalance, 0.001)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun createPortfolio_callsRepository() = runTest {
        val portfolioRepo = mockk<PortfolioRepository>(relaxed = true) {
            every { getAllPortfolios() } returns flowOf(emptyList())
            every { getAllTrades() } returns flowOf(emptyList())
        }
        val stockRepo = mockk<StockRepository>(relaxed = true) {
            every { getStocks() } returns flowOf(emptyList())
        }
        val viewModel = PortfolioViewModel(portfolioRepo, stockRepo)

        viewModel.createPortfolio("My Portfolio", "Scalps", 5000.0)

        coVerify {
            portfolioRepo.createPortfolio(
                match { it.name == "My Portfolio" && it.startingBalance == 5000.0 }
            )
        }
    }
}