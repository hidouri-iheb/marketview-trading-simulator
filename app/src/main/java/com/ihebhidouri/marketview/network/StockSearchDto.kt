package com.ihebhidouri.marketview.network


import com.google.gson.annotations.SerializedName

data class StockSearchResponseDto(
    val data: List<StockSearchItemDto> = emptyList(),
    val status: String? = null
)

data class StockSearchItemDto(
    val symbol: String? = null,

    @SerializedName("instrument_name")
    val name: String? = null,

    val currency: String? = null,
    val exchange: String? = null,

    @SerializedName("mic_code")
    val micCode: String? = null,

    val country: String? = null,
    val type: String? = null
)