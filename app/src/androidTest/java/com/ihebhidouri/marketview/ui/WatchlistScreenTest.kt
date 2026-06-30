package com.ihebhidouri.marketview.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.ihebhidouri.marketview.models.Stock
import com.ihebhidouri.marketview.ui.screens.WatchlistScreen
import com.ihebhidouri.marketview.ui.theme.MarketViewTheme
import com.ihebhidouri.marketview.viewmodels.WatchlistUiState
import org.junit.Rule
import org.junit.Test

class WatchlistScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun sampleStock() = Stock(
        symbol = "AAPL",
        name = "Apple Inc.",
        exchange = "NASDAQ",
        currency = "USD",
        price = 198.00,
        change = 1.20,
        changePercent = 0.61,
        open = 197.0,
        high = 199.0,
        low = 196.0,
        previousClose = 196.8,
        volume = 55_000_000,
        fiftyTwoWeekHigh = 220.0,
        fiftyTwoWeekLow = 160.0
    )

    @Test
    fun watchlistScreen_displaysSubtitle() {
        composeTestRule.setContent {
            MarketViewTheme {
                WatchlistScreen(
                    uiState = WatchlistUiState(stocks = emptyList()),
                    onRemoveStock = {},
                    portfolios = emptyList(),
                    onOpenTrade = { _, _, _, _, _, _, _, _ -> }
                )
            }
        }
        composeTestRule.onNodeWithText("Your tracked assets with live prices.").assertIsDisplayed()
    }

    @Test
    fun watchlistScreen_emptyState_showsHeader() {
        composeTestRule.setContent {
            MarketViewTheme {
                WatchlistScreen(
                    uiState = WatchlistUiState(stocks = emptyList()),
                    onRemoveStock = {},
                    portfolios = emptyList(),
                    onOpenTrade = { _, _, _, _, _, _, _, _ -> }
                )
            }
        }
        composeTestRule.onNodeWithText("Watchlist").assertIsDisplayed()
    }

    @Test
    fun watchlistScreen_populated_displaysStock() {
        composeTestRule.setContent {
            MarketViewTheme {
                WatchlistScreen(
                    uiState = WatchlistUiState(stocks = listOf(sampleStock())),
                    onRemoveStock = {},
                    portfolios = emptyList(),
                    onOpenTrade = { _, _, _, _, _, _, _, _ -> }
                )
            }
        }
        composeTestRule.onNodeWithText("Apple Inc.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Trade").assertIsDisplayed()
    }
}