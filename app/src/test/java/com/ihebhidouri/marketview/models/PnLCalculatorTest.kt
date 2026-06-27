package com.ihebhidouri.marketview.models

import com.ihebhidouri.marketview.data.room.entity.Trade
import org.junit.Assert.assertEquals
import org.junit.Test
import com.ihebhidouri.marketview.data.room.entity.Portfolio
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse


class PnLCalculatorTest {

    private val calculator = PnLCalculator()

    private fun trade(
        type: String,
        entryPrice: Double,
        size: Double,
        leverage: Double,
        isOpen: Boolean = true,
        exitPrice: Double? = null
    ) = Trade(
        id = 1L,
        portfolioId = 1L,
        symbol = "AAPL",
        name = "Apple Inc.",
        type = type,
        size = size,
        leverage = leverage,
        entryPrice = entryPrice,
        takeProfit = null,
        stopLoss = null,
        exitPrice = exitPrice,
        isOpen = isOpen
    )

    @Test
    fun buy_priceGoesUp_positivePnL() {
        val result = calculator.calculate(
            trade("BUY", entryPrice = 100.0, size = 10.0, leverage = 1.0),
            currentPrice = 110.0
        )
        assertEquals(100.0, result, 0.001)
    }

    @Test
    fun buy_priceGoesDown_negativePnL() {
        val result = calculator.calculate(
            trade("BUY", entryPrice = 100.0, size = 10.0, leverage = 1.0),
            currentPrice = 90.0
        )
        assertEquals(-100.0, result, 0.001)
    }

    @Test
    fun sell_priceGoesUp_negativePnL() {
        val result = calculator.calculate(
            trade("SELL", entryPrice = 100.0, size = 10.0, leverage = 1.0),
            currentPrice = 110.0
        )
        assertEquals(-100.0, result, 0.001)
    }

    @Test
    fun sell_priceGoesDown_positivePnL() {
        val result = calculator.calculate(
            trade("SELL", entryPrice = 100.0, size = 10.0, leverage = 1.0),
            currentPrice = 90.0
        )
        assertEquals(100.0, result, 0.001)
    }

    @Test
    fun leverage_multipliesPnL() {
        val result = calculator.calculate(
            trade("BUY", entryPrice = 100.0, size = 10.0, leverage = 5.0),
            currentPrice = 110.0
        )
        assertEquals(500.0, result, 0.001)
    }

    @Test
    fun closedTrade_usesExitPrice_notCurrentPrice() {
        val result = calculator.calculate(
            trade("BUY", entryPrice = 100.0, size = 10.0, leverage = 1.0, isOpen = false, exitPrice = 105.0),
            currentPrice = 999.0
        )
        assertEquals(50.0, result, 0.001)
    }

    @Test
    fun noChange_zeroPnL() {
        val result = calculator.calculate(
            trade("BUY", entryPrice = 100.0, size = 10.0, leverage = 1.0),
            currentPrice = 100.0
        )
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun unknownType_returnsZero() {
        val result = calculator.calculate(
            trade("HOLD", entryPrice = 100.0, size = 10.0, leverage = 1.0),
            currentPrice = 200.0
        )
        assertEquals(0.0, result, 0.001)
    }
    // buildSummaries tests

    private fun portfolio(
        id: Long,
        startingBalance: Double,
        realizedPnL: Double = 0.0
    ) = Portfolio(
        id = id,
        userId = "user1",
        ownerName = "TestUser",
        name = "Portfolio $id",
        style = "Swing",
        startingBalance = startingBalance,
        realizedPnL = realizedPnL
    )

    @Test
    fun buildSummaries_noTrades_balanceUnchanged() {
        val portfolios = listOf(portfolio(1L, startingBalance = 10000.0))
        val result = calculator.buildSummaries(portfolios, emptyList(), emptyMap())

        assertEquals(10000.0, result[0].currentBalance, 0.001)
        assertEquals(0.0, result[0].pnlPercent, 0.001)
    }

    @Test
    fun buildSummaries_withOpenTrade_reflectsUnrealizedPnL() {
        val portfolios = listOf(portfolio(1L, startingBalance = 10000.0))
        val trades = listOf(
            trade("BUY", entryPrice = 100.0, size = 10.0, leverage = 1.0)
        )
        val livePrices = mapOf("AAPL" to 110.0)

        val result = calculator.buildSummaries(portfolios, trades, livePrices)

        assertEquals(10100.0, result[0].currentBalance, 0.001)
        assertEquals(1.0, result[0].pnlPercent, 0.001)
    }

    @Test
    fun buildSummaries_withRealizedPnL_addsBoth() {
        val portfolios = listOf(portfolio(1L, startingBalance = 10000.0, realizedPnL = 200.0))
        val trades = listOf(
            trade("BUY", entryPrice = 100.0, size = 10.0, leverage = 1.0)
        )
        val livePrices = mapOf("AAPL" to 110.0)

        val result = calculator.buildSummaries(portfolios, trades, livePrices)

        assertEquals(10300.0, result[0].currentBalance, 0.001)
        assertEquals(3.0, result[0].pnlPercent, 0.001)
    }

    @Test
    fun buildSummaries_multiplePortfolios_filteredCorrectly() {
        val portfolios = listOf(
            portfolio(1L, startingBalance = 10000.0),
            portfolio(2L, startingBalance = 5000.0)
        )
        val trades = listOf(
            trade("BUY", entryPrice = 100.0, size = 10.0, leverage = 1.0)
        )
        val livePrices = mapOf("AAPL" to 110.0)

        val result = calculator.buildSummaries(portfolios, trades, livePrices)

        assertEquals(10100.0, result[0].currentBalance, 0.001)
        assertEquals(5000.0, result[1].currentBalance, 0.001)
    }
    // TP/SL auto-close tests

    @Test
    fun autoClose_buyTrade_tpHit_shouldClose() {
        val t = trade("BUY", entryPrice = 100.0, size = 10.0, leverage = 1.0)
            .copy(takeProfit = 110.0)
        val result = calculator.shouldAutoClose(t, currentPrice = 112.0)

        assertTrue(result.shouldClose)
        assertEquals(110.0, result.closePrice, 0.001)
        assertEquals("TP", result.reason)
    }

    @Test
    fun autoClose_buyTrade_slHit_shouldClose() {
        val t = trade("BUY", entryPrice = 100.0, size = 10.0, leverage = 1.0)
            .copy(stopLoss = 90.0)
        val result = calculator.shouldAutoClose(t, currentPrice = 88.0)

        assertTrue(result.shouldClose)
        assertEquals(90.0, result.closePrice, 0.001)
        assertEquals("SL", result.reason)
    }

    @Test
    fun autoClose_sellTrade_tpHit_shouldClose() {
        val t = trade("SELL", entryPrice = 100.0, size = 10.0, leverage = 1.0)
            .copy(takeProfit = 90.0)
        val result = calculator.shouldAutoClose(t, currentPrice = 88.0)

        assertTrue(result.shouldClose)
        assertEquals(90.0, result.closePrice, 0.001)
    }

    @Test
    fun autoClose_sellTrade_slHit_shouldClose() {
        val t = trade("SELL", entryPrice = 100.0, size = 10.0, leverage = 1.0)
            .copy(stopLoss = 110.0)
        val result = calculator.shouldAutoClose(t, currentPrice = 112.0)

        assertTrue(result.shouldClose)
        assertEquals(110.0, result.closePrice, 0.001)
    }

    @Test
    fun autoClose_priceNotReached_shouldNotClose() {
        val t = trade("BUY", entryPrice = 100.0, size = 10.0, leverage = 1.0)
            .copy(takeProfit = 110.0, stopLoss = 90.0)
        val result = calculator.shouldAutoClose(t, currentPrice = 105.0)

        assertFalse(result.shouldClose)
    }

    @Test
    fun autoClose_noTpNoSl_shouldNotClose() {
        val t = trade("BUY", entryPrice = 100.0, size = 10.0, leverage = 1.0)
        val result = calculator.shouldAutoClose(t, currentPrice = 200.0)

        assertFalse(result.shouldClose)
    }
}