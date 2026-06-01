package com.ihebhidouri.marketview.repository

import com.ihebhidouri.marketview.data.remote.FiftyTwoWeek
import com.ihebhidouri.marketview.data.remote.QuoteResponse
import com.ihebhidouri.marketview.data.remote.TwelveDataApi
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class RealStockRepositoryTest {

    private val api = mockk<TwelveDataApi>()
    private val repository = RealStockRepository(api, apiKey = "fake")

    @Test
    fun mapToStock_validDto_mapsCorrectly() {
        val dto = QuoteResponse(
            symbol = "AAPL",
            name = "Apple Inc.",
            exchange = "NASDAQ",
            currency = "USD",
            open = "195.00",
            high = "200.00",
            low = "194.00",
            close = "198.50",
            volume = "55000000",
            previousClose = "196.20",
            change = "2.30",
            percentChange = "1.17",
            fiftyTwoWeek = FiftyTwoWeek(low = "160.00", high = "220.00")
        )

        val stock = repository.mapToStock(dto)

        assertEquals("AAPL", stock.symbol)
        assertEquals("Apple Inc.", stock.name)
        assertEquals(198.50, stock.price, 0.001)
        assertEquals(2.30, stock.change, 0.001)
        assertEquals(1.17, stock.changePercent, 0.001)
        assertEquals(195.00, stock.open, 0.001)
        assertEquals(200.00, stock.high, 0.001)
        assertEquals(194.00, stock.low, 0.001)
        assertEquals(196.20, stock.previousClose, 0.001)
        assertEquals(55000000L, stock.volume)
        assertEquals(220.00, stock.fiftyTwoWeekHigh, 0.001)
        assertEquals(160.00, stock.fiftyTwoWeekLow, 0.001)
    }
}