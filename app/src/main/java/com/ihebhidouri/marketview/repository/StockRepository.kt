package com.ihebhidouri.marketview.repository

import com.ihebhidouri.marketview.BuildConfig
import com.ihebhidouri.marketview.data.StockSymbols
import com.ihebhidouri.marketview.data.remote.QuoteResponse
import com.ihebhidouri.marketview.data.remote.RetrofitClient
import com.ihebhidouri.marketview.models.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StockRepository {

    private val api = RetrofitClient.api
    private val apiKey = BuildConfig.TWELVE_DATA_API_KEY

    fun getStocks(): Flow<List<Stock>> = flow {
        val stocks = mutableListOf<Stock>()

        for (symbol in StockSymbols.TOP_STOCKS) {
            try {
                val quote = api.getQuote(symbol = symbol, apiKey = apiKey)
                val stock = mapToStock(quote)
                stocks.add(stock)
            } catch (e: Exception) {
                // Skip failed symbols
            }
        }

        emit(stocks)
    }

    suspend fun getStockDetail(symbol: String): Stock? {
        return try {
            val quote = api.getQuote(symbol = symbol, apiKey = apiKey)
            mapToStock(quote)
        } catch (e: Exception) {
            null
        }
    }

    private fun mapToStock(dto: QuoteResponse): Stock {
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