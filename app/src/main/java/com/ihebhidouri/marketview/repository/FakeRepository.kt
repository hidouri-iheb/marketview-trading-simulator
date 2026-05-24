package com.ihebhidouri.marketview.repository

import com.ihebhidouri.marketview.models.Stock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class FakeStockRepository : StockRepository {

    private val baseStocks = listOf(
        Stock("TSLA", "Tesla, Inc.", "NASDAQ", "USD", 426.01, 8.15, 1.95, 420.00, 430.50, 418.20, 417.86, 48937253, 488.54, 138.80),
        Stock("NVDA", "NVIDIA Corporation", "NASDAQ", "USD", 215.33, -4.27, -1.94, 218.00, 220.10, 214.50, 219.60, 52341000, 346.47, 75.61),
        Stock("AAPL", "Apple Inc.", "NASDAQ", "USD", 308.82, 3.84, 1.26, 305.00, 310.20, 304.50, 304.98, 35127800, 313.00, 184.80),
        Stock("GOOGL", "Alphabet Inc.", "NASDAQ", "USD", 382.97, -4.67, -1.21, 386.00, 388.40, 381.20, 387.64, 21456000, 415.00, 150.22),
        Stock("AMZN", "Amazon.com, Inc.", "NASDAQ", "USD", 266.32, -2.18, -0.81, 268.00, 270.10, 265.50, 268.50, 31245000, 278.00, 151.61),
        Stock("NFLX", "Netflix, Inc.", "NASDAQ", "USD", 88.60, -0.69, -0.78, 89.00, 90.20, 88.10, 89.29, 8923000, 108.54, 65.38),
        Stock("META", "Meta Platforms Inc.", "NASDAQ", "USD", 610.26, 2.84, 0.47, 607.00, 613.50, 605.80, 607.42, 18234000, 638.40, 414.50),
        Stock("MSFT", "Microsoft Corp.", "NASDAQ", "USD", 418.57, -0.52, -0.12, 419.00, 421.30, 417.80, 419.09, 22145000, 468.35, 385.58)
    )

    private val _prices = MutableStateFlow<List<Stock>>(emptyList())

    init {
        CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val updated = baseStocks.map { stock ->
                    val change = Random.nextDouble(-2.0, 2.0)
                    val newPrice = stock.price + change
                    stock.copy(
                        price = newPrice,
                        change = change,
                        changePercent = (change / stock.price) * 100
                    )
                }
                _prices.value = updated
                delay(3000)
            }
        }
    }

    override fun getStocks(): Flow<List<Stock>> = _prices

    override suspend fun getStockDetail(symbol: String): Stock? {
        return _prices.value.find { it.symbol == symbol }
            ?: baseStocks.find { it.symbol == symbol }
    }
}