package com.ihebhidouri.marketview.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.ihebhidouri.marketview.data.room.entity.Portfolio
import com.ihebhidouri.marketview.data.room.entity.Trade
import com.ihebhidouri.marketview.models.PortfolioSummary
import com.ihebhidouri.marketview.ui.screens.HomeScreen
import com.ihebhidouri.marketview.ui.theme.MarketViewTheme
import com.ihebhidouri.marketview.viewmodels.StockUiState
import com.ihebhidouri.marketview.viewmodels.TradeWithPnL
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun portfolio(id: Long, ownerName: String, name: String, pnlPercent: Double, balance: Double) =
        PortfolioSummary(
            portfolio = Portfolio(
                id = id,
                userId = "user_$id",
                ownerName = ownerName,
                name = name,
                style = "Swing",
                startingBalance = 10000.0
            ),
            pnlPercent = pnlPercent,
            currentBalance = balance,
            ownerName = ownerName
        )

    private fun openTrade(id: Long, symbol: String, type: String, entryPrice: Double, currentPrice: Double, pnl: Double) =
        TradeWithPnL(
            trade = Trade(
                id = id,
                portfolioId = 1L,
                symbol = symbol,
                name = "$symbol Company",
                type = type,
                size = 10.0,
                leverage = 1.0,
                entryPrice = entryPrice,
                takeProfit = null,
                stopLoss = null,
                exitPrice = null,
                isOpen = true
            ),
            currentPrice = currentPrice,
            pnl = pnl
        )

    @Test
    fun homeScreen_leaderboardShowsRankedPortfolios() {
        val leaderboard = listOf(
            portfolio(1L, "Marco Rossi", "Alpha Fund", 28.4, 64200.0),
            portfolio(2L, "Fatma Ben Ali", "Balanced Growth", 11.2, 27800.0),
            portfolio(3L, "Youssef Hammami", "YOLO Trades", -19.0, 40500.0)
        )

        composeTestRule.setContent {
            MarketViewTheme {
                HomeScreen(
                    uiState = StockUiState(),
                    searchQuery = "",
                    searchResults = emptyList(),
                    selectedStock = null,
                    isCardLoading = false,
                    onSearchQueryChange = {},
                    onStockSelected = {},
                    onDismissCard = {},
                    onAddToWatchlist = {},
                    onRetryLoadStocks = {},
                    openTrades = emptyList(),
                    leaderboard = leaderboard,
                    displayName = null,
                    onNavigateToPortfolio = {},
                    onCloseTrade = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("#1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Marco Rossi").assertIsDisplayed()
        composeTestRule.onNodeWithText("#2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fatma Ben Ali").assertIsDisplayed()
        composeTestRule.onNodeWithText("#3").assertIsDisplayed()
        composeTestRule.onNodeWithText("Youssef Hammami").assertIsDisplayed()
    }

    @Test
    fun homeScreen_withOpenTrades_displaysTradeInfo() {
        val trades = listOf(
            openTrade(1L, "AAPL", "BUY", 190.0, 198.0, 80.0),
            openTrade(2L, "TSLA", "SELL", 180.0, 175.0, 50.0)
        )

        composeTestRule.setContent {
            MarketViewTheme {
                HomeScreen(
                    uiState = StockUiState(),
                    searchQuery = "",
                    searchResults = emptyList(),
                    selectedStock = null,
                    isCardLoading = false,
                    onSearchQueryChange = {},
                    onStockSelected = {},
                    onDismissCard = {},
                    onAddToWatchlist = {},
                    onRetryLoadStocks = {},
                    openTrades = trades,
                    leaderboard = emptyList(),
                    displayName = null,
                    onNavigateToPortfolio = {},
                    onCloseTrade = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("AAPL").assertIsDisplayed()
        composeTestRule.onNodeWithText("TSLA").assertIsDisplayed()
    }

    @Test
    fun homeScreen_errorState_showsRetryButton() {
        composeTestRule.setContent {
            MarketViewTheme {
                HomeScreen(
                    uiState = StockUiState(error = "Network error"),
                    searchQuery = "",
                    searchResults = emptyList(),
                    selectedStock = null,
                    isCardLoading = false,
                    onSearchQueryChange = {},
                    onStockSelected = {},
                    onDismissCard = {},
                    onAddToWatchlist = {},
                    onRetryLoadStocks = {},
                    openTrades = emptyList(),
                    leaderboard = emptyList(),
                    displayName = null,
                    onNavigateToPortfolio = {},
                    onCloseTrade = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }
}