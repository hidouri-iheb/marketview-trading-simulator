package com.ihebhidouri.marketview.repository

import com.ihebhidouri.marketview.models.Stock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.util.Random
import kotlin.math.abs

class FakeStockRepository(scope: CoroutineScope) : StockRepository {

    private data class StockSeed(
        val symbol: String,
        val name: String,
        val exchange: String,
        val basePrice: Double,
        val volatility: Double,
        val fiftyTwoWeekHigh: Double,
        val fiftyTwoWeekLow: Double,
        val avgVolume: Long
    )

    private val seeds = listOf(
        StockSeed("AAPL", "Apple Inc.", "NASDAQ", 198.00, 0.008, 220.00, 160.00, 55_000_000),
        StockSeed("MSFT", "Microsoft Corp.", "NASDAQ", 425.00, 0.009, 468.00, 380.00, 22_000_000),
        StockSeed("GOOGL", "Alphabet Inc.", "NASDAQ", 175.00, 0.010, 192.00, 140.00, 25_000_000),
        StockSeed("AMZN", "Amazon.com, Inc.", "NASDAQ", 186.00, 0.011, 201.00, 150.00, 35_000_000),
        StockSeed("TSLA", "Tesla, Inc.", "NASDAQ", 178.00, 0.018, 270.00, 130.00, 60_000_000),
        StockSeed("META", "Meta Platforms Inc.", "NASDAQ", 505.00, 0.012, 602.00, 390.00, 18_000_000),
        StockSeed("NVDA", "NVIDIA Corporation", "NASDAQ", 880.00, 0.016, 974.00, 550.00, 40_000_000),
        StockSeed("NFLX", "Netflix, Inc.", "NASDAQ", 620.00, 0.013, 700.00, 500.00, 8_000_000),
        StockSeed("AMD", "Advanced Micro Devices", "NASDAQ", 155.00, 0.015, 187.00, 120.00, 45_000_000),
        StockSeed("INTC", "Intel Corporation", "NASDAQ", 30.50, 0.014, 45.00, 20.00, 35_000_000),
        StockSeed("CRM", "Salesforce Inc.", "NYSE", 270.00, 0.011, 318.00, 210.00, 7_000_000),
        StockSeed("ORCL", "Oracle Corporation", "NYSE", 125.00, 0.010, 140.00, 100.00, 10_000_000),
        StockSeed("CSCO", "Cisco Systems, Inc.", "NASDAQ", 50.00, 0.008, 58.00, 44.00, 20_000_000),
        StockSeed("ADBE", "Adobe Inc.", "NASDAQ", 475.00, 0.012, 570.00, 400.00, 4_000_000),
        StockSeed("PYPL", "PayPal Holdings", "NASDAQ", 66.00, 0.013, 85.00, 55.00, 15_000_000),
        StockSeed("UBER", "Uber Technologies", "NYSE", 75.00, 0.014, 87.00, 55.00, 20_000_000),
        StockSeed("SQ", "Block, Inc.", "NYSE", 80.00, 0.016, 95.00, 55.00, 10_000_000),
        StockSeed("SHOP", "Shopify Inc.", "NYSE", 85.00, 0.015, 105.00, 55.00, 8_000_000),
        StockSeed("SNAP", "Snap Inc.", "NYSE", 12.50, 0.020, 17.00, 8.00, 25_000_000),
        StockSeed("SPOT", "Spotify Technology", "NYSE", 310.00, 0.013, 370.00, 210.00, 3_000_000),
        StockSeed("DIS", "Walt Disney Co.", "NYSE", 112.00, 0.011, 125.00, 83.00, 12_000_000),
        StockSeed("V", "Visa Inc.", "NYSE", 280.00, 0.007, 305.00, 250.00, 8_000_000),
        StockSeed("MA", "Mastercard Inc.", "NYSE", 460.00, 0.008, 500.00, 400.00, 4_000_000),
        StockSeed("JPM", "JPMorgan Chase & Co.", "NYSE", 195.00, 0.009, 215.00, 165.00, 10_000_000),
        StockSeed("BAC", "Bank of America Corp.", "NYSE", 37.00, 0.011, 44.00, 30.00, 35_000_000),
        StockSeed("GS", "Goldman Sachs Group", "NYSE", 430.00, 0.012, 490.00, 350.00, 3_000_000),
        StockSeed("WMT", "Walmart Inc.", "NYSE", 165.00, 0.007, 180.00, 145.00, 8_000_000),
        StockSeed("KO", "Coca-Cola Co.", "NYSE", 62.00, 0.006, 68.00, 55.00, 15_000_000),
        StockSeed("PEP", "PepsiCo Inc.", "NYSE", 175.00, 0.007, 190.00, 155.00, 6_000_000),
        StockSeed("MCD", "McDonald's Corp.", "NYSE", 290.00, 0.008, 310.00, 245.00, 4_000_000),
        StockSeed("NKE", "Nike Inc.", "NYSE", 98.00, 0.012, 120.00, 70.00, 10_000_000),
        StockSeed("JNJ", "Johnson & Johnson", "NYSE", 155.00, 0.007, 170.00, 140.00, 7_000_000),
        StockSeed("PFE", "Pfizer Inc.", "NYSE", 28.00, 0.011, 35.00, 25.00, 30_000_000),
        StockSeed("XOM", "Exxon Mobil Corp.", "NYSE", 110.00, 0.010, 125.00, 95.00, 15_000_000),
        StockSeed("CVX", "Chevron Corp.", "NYSE", 155.00, 0.010, 175.00, 135.00, 8_000_000),
        StockSeed("BA", "Boeing Co.", "NYSE", 185.00, 0.016, 220.00, 140.00, 6_000_000),
        StockSeed("COST", "Costco Wholesale", "NASDAQ", 730.00, 0.008, 790.00, 620.00, 3_000_000),
        StockSeed("HD", "Home Depot Inc.", "NYSE", 345.00, 0.009, 390.00, 300.00, 5_000_000),
        StockSeed("LLY", "Eli Lilly & Co.", "NYSE", 780.00, 0.013, 900.00, 540.00, 4_000_000),
        StockSeed("ABNB", "Airbnb Inc.", "NASDAQ", 155.00, 0.015, 175.00, 120.00, 6_000_000),
        StockSeed("COIN", "Coinbase Global", "NASDAQ", 225.00, 0.022, 280.00, 115.00, 8_000_000),
        StockSeed("PLTR", "Palantir Technologies", "NYSE", 24.00, 0.019, 30.00, 15.00, 40_000_000),
        StockSeed("RIVN", "Rivian Automotive", "NASDAQ", 12.00, 0.024, 22.00, 8.00, 25_000_000),
        StockSeed("SOFI", "SoFi Technologies", "NASDAQ", 8.50, 0.021, 12.00, 6.00, 30_000_000),
        StockSeed("MSTR", "MicroStrategy Inc.", "NASDAQ", 1500.00, 0.015, 1800.00, 1000.00, 5_000_000),
        StockSeed("LCID", "Lucid Group Inc.", "NASDAQ", 3.50, 0.018, 6.00, 2.00, 35_000_000)
    )


    private data class LiveState(
        val changePercent: Double,
        val previousClose: Double,
        val open: Double,
        val high: Double,
        val low: Double,
        val volume: Long,
        val trend: Double
    )

    private val random = Random()
    private val liveStates = mutableMapOf<String, LiveState>()

    init {
        seeds.forEach { seed ->
            val changePercent = (random.nextGaussian() * seed.volatility * 100.0)
                .coerceIn(-3.0, 3.0)

            val previousClose = seed.basePrice / (1.0 + changePercent / 100.0)
            val currentPrice = seed.basePrice
            val openOffset = previousClose * (1.0 + changePercent * (0.6 + random.nextDouble() * 0.2) / 100.0)

            liveStates[seed.symbol] = LiveState(
                changePercent = changePercent,
                previousClose = previousClose,
                open = openOffset,
                high = maxOf(currentPrice, openOffset) * (1.0 + random.nextDouble() * 0.001),
                low = minOf(currentPrice, openOffset) * (1.0 - random.nextDouble() * 0.001),
                volume = (seed.avgVolume * (0.3 + random.nextDouble() * 0.3)).toLong(),
                trend = 0.0
            )
        }
    }

    private fun simulateTick() {
        seeds.forEach { seed ->
            val state = liveStates[seed.symbol] ?: return@forEach

            val forcedTrend = when (seed.symbol) {
                "MSTR" -> 0.02   // gentle sustained bullish drift
                "LCID" -> -0.02  // gentle sustained bearish drift
                else -> 0.0
            }

            val newTrend = state.trend * 0.95 + random.nextGaussian() * 0.005 + forcedTrend
            val nudge = newTrend + random.nextGaussian() * 0.08
            val cappedChange = (state.changePercent + nudge).coerceIn(-15.0, 15.0)

            val newPrice = state.previousClose * (1.0 + cappedChange / 100.0)
            val rebased = abs(cappedChange) >= 14.5

            // Re-baseline at the band edge: bank the move into a new base price and
            // reset the day-% to 0 so trends compound across sessions instead of
            // freezing at ±15%. Mirrors how a real stock's close becomes the next
            // session's reference price. The displayed price stays continuous.
            liveStates[seed.symbol] = state.copy(
                changePercent = if (rebased) 0.0 else cappedChange,
                previousClose = if (rebased) newPrice else state.previousClose,
                high = if (rebased) newPrice else maxOf(state.high, newPrice),
                low = if (rebased) newPrice else minOf(state.low, newPrice),
                volume = state.volume + (5_000 + (random.nextDouble() * 45_000).toLong()),
                trend = newTrend
            )
        }
    }
    private fun buildStockList(): List<Stock> {
        return seeds.map { seed ->
            val state = liveStates[seed.symbol]!!
            val price = state.previousClose * (1.0 + state.changePercent / 100.0)
            val change = price - state.previousClose

            Stock(
                symbol = seed.symbol,
                name = seed.name,
                exchange = seed.exchange,
                currency = "USD",
                price = price,
                change = change,
                changePercent = state.changePercent,
                open = state.open,
                high = state.high,
                low = state.low,
                previousClose = state.previousClose,
                volume = state.volume,
                fiftyTwoWeekHigh = seed.fiftyTwoWeekHigh,
                fiftyTwoWeekLow = seed.fiftyTwoWeekLow
            )
        }
    }

    private val sharedStocks: StateFlow<List<Stock>> = flow {
        while (true) {
            simulateTick()
            emit(buildStockList())
            delay(500)
        }
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = buildStockList()
    )

    override fun getStocks(): Flow<List<Stock>> = sharedStocks

    override suspend fun getStockDetail(symbol: String): Stock? {
        return sharedStocks.value.find { it.symbol == symbol }
    }
}