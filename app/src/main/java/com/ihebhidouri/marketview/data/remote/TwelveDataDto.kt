package com.ihebhidouri.marketview.data.remote

import com.google.gson.annotations.SerializedName

data class QuoteResponse(
    val symbol: String,
    val name: String,
    val exchange: String,
    val currency: String,
    val open: String,
    val high: String,
    val low: String,
    val close: String,
    val volume: String,
    @SerializedName("previous_close")
    val previousClose: String,
    val change: String,
    @SerializedName("percent_change")
    val percentChange: String,
    @SerializedName("fifty_two_week")
    val fiftyTwoWeek: FiftyTwoWeek?
)

data class FiftyTwoWeek(
    val low: String,
    val high: String
)
data class LogoResponse(
    val url: String
)