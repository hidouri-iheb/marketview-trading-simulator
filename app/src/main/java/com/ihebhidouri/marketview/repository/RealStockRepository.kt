package com.ihebhidouri.marketview.repository

import com.ihebhidouri.marketview.BuildConfig
import com.ihebhidouri.marketview.data.SearchableStocks
import com.ihebhidouri.marketview.data.remote.QuoteResponse
import com.ihebhidouri.marketview.data.remote.TwelveDataApi
import com.ihebhidouri.marketview.models.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay


class RealStockRepository(
    private val api: TwelveDataApi,
    private val apiKey: String = BuildConfig.TWELVE_DATA_API_KEY
) : StockRepository {

    override fun getStocks(): Flow<List<Stock>> = flow {
        while (true) {
            val stocks = mutableListOf<Stock>()

            for (symbol in SearchableStocks.ALL.map { it.symbol }) {
                try {
                    val quote = api.getQuote(symbol = symbol, apiKey = apiKey)
                    stocks.add(mapToStock(quote))
                } catch (e: Exception) {
                    println("Failed to fetch $symbol: ${e.message}")
                }
            }

            emit(stocks)
            delay(60_000)
        }
    }

    override suspend fun getStockDetail(symbol: String): Stock? {
        return try {
            val quote = api.getQuote(symbol = symbol, apiKey = apiKey)
            mapToStock(quote)
        } catch (e: Exception) {
            println("Failed to fetch detail for $symbol: ${e.message}")
            null
        }
    }

    internal fun mapToStock(dto: QuoteResponse): Stock {
        return Stock(
            symbol = dto.symbol,
            name = dto.name,
            exchange = dto.exchange,
            currency = dto.currency,
            price = dto.close.toDoubleOrNull() ?: 0.0,
            change = dto.change.toDoubleOrNull() ?: 0.0,
            changePercent = dto.percentChange.toDoubleOrNull() ?: 0.0,
            open = dto.open.toDoubleOrNull() ?: 0.0,
            high = dto.high.toDoubleOrNull() ?: 0.0,
            low = dto.low.toDoubleOrNull() ?: 0.0,
            previousClose = dto.previousClose.toDoubleOrNull() ?: 0.0,
            volume = dto.volume.toLongOrNull() ?: 0L,
            fiftyTwoWeekHigh = dto.fiftyTwoWeek?.high?.toDoubleOrNull() ?: 0.0,
            fiftyTwoWeekLow = dto.fiftyTwoWeek?.low?.toDoubleOrNull() ?: 0.0,

        )
    }
}