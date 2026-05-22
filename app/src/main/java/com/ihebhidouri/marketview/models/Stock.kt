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
    val fiftyTwoWeekLow: Double,

)