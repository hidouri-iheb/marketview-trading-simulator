package com.ihebhidouri.marketview.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.ihebhidouri.marketview.data.room.entity.Portfolio
import com.ihebhidouri.marketview.models.PortfolioSummary
import com.ihebhidouri.marketview.models.Stock
import com.ihebhidouri.marketview.ui.components.TradeDialog
import com.ihebhidouri.marketview.ui.theme.MarketViewTheme
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.onAllNodesWithText

class TradeDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testStock = Stock(
        symbol = "AAPL",
        name = "Apple Inc.",
        exchange = "NASDAQ",
        currency = "USD",
        price = 198.0,
        change = 2.0,
        changePercent = 1.5,
        open = 196.0,
        high = 200.0,
        low = 195.0,
        previousClose = 196.0,
        volume = 55000000,
        fiftyTwoWeekHigh = 220.0,
        fiftyTwoWeekLow = 160.0
    )

    private val testPortfolio = PortfolioSummary(
        portfolio = Portfolio(
            id = 1L,
            userId = "user1",
            ownerName = "TestUser",
            name = "Test Portfolio",
            style = "Swing",
            startingBalance = 10000.0
        ),
        pnlPercent = 0.0,
        currentBalance = 10000.0
    )

    @Test
    fun tradeDialog_displaysTitle() {
        composeTestRule.setContent {
            MarketViewTheme {
                TradeDialog(
                    portfolios = emptyList(),
                    watchlistStocks = emptyList(),
                    onDismiss = {},
                    onConfirm = { _, _, _, _, _, _, _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Open Trade").assertIsDisplayed()
    }

    @Test
    fun tradeDialog_noPortfolios_showsError() {
        composeTestRule.setContent {
            MarketViewTheme {
                TradeDialog(
                    portfolios = emptyList(),
                    watchlistStocks = emptyList(),
                    onDismiss = {},
                    onConfirm = { _, _, _, _, _, _, _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Create a portfolio first.").assertIsDisplayed()
    }

    @Test
    fun tradeDialog_withPortfolio_showsBuySellOptions() {
        composeTestRule.setContent {
            MarketViewTheme {
                TradeDialog(
                    portfolios = listOf(testPortfolio),
                    watchlistStocks = listOf(testStock),
                    onDismiss = {},
                    onConfirm = { _, _, _, _, _, _, _, _ -> }
                )
            }
        }

        composeTestRule.onAllNodesWithText("BUY")[0].assertIsDisplayed()
        composeTestRule.onAllNodesWithText("SELL")[0].assertIsDisplayed()
    }

    @Test
    fun tradeDialog_withPreSelectedStock_displaysSymbol() {
        composeTestRule.setContent {
            MarketViewTheme {
                TradeDialog(
                    portfolios = listOf(testPortfolio),
                    watchlistStocks = listOf(testStock),
                    preSelectedStock = testStock,
                    onDismiss = {},
                    onConfirm = { _, _, _, _, _, _, _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("AAPL", substring = true).assertIsDisplayed()
    }
}