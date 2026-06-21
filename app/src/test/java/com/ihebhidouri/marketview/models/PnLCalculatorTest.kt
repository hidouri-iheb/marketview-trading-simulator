package com.ihebhidouri.marketview.models

import com.ihebhidouri.marketview.data.local.Trade
import org.junit.Assert.assertEquals
import org.junit.Test

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
}