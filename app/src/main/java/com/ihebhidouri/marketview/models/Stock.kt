package com.ihebhidouri.marketview.models

data class Stock(
    val symbol: String,
    val name: String,
    val exchange: String,
    val currency: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val open: Double,
    val high: Double,
    val low: Double,
    val previousClose: Double,
    val volume: Long,
    val fiftyTwoWeekHigh: Double,
    val fiftyTwoWeekLow: Double
) {
    val isPositive: Boolean get() = changePercent >= 0

    val formattedPrice: String get() = "$${String.format("%.2f", price)}"

    val formattedChangePercent: String
        get() = "${if (isPositive) "+" else ""}${String.format("%.2f", changePercent)}%"

    val formattedChange: String
        get() = "${if (isPositive) "+" else ""}${String.format("%.2f", change)}"
}