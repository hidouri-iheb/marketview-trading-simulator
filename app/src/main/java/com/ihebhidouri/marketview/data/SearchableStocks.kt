package com.ihebhidouri.marketview.data

data class SearchableStock(
    val symbol: String,
    val name: String
)

object SearchableStocks {
    val ALL = listOf(
        SearchableStock("AAPL", "Apple Inc."),
        SearchableStock("MSFT", "Microsoft Corp."),
        SearchableStock("GOOGL", "Alphabet Inc."),
        SearchableStock("AMZN", "Amazon.com, Inc."),
        SearchableStock("TSLA", "Tesla, Inc."),
        SearchableStock("META", "Meta Platforms Inc."),
        SearchableStock("NVDA", "NVIDIA Corporation"),
        SearchableStock("NFLX", "Netflix, Inc."),
        SearchableStock("AMD", "Advanced Micro Devices"),
        SearchableStock("INTC", "Intel Corporation"),
        SearchableStock("CRM", "Salesforce Inc."),
        SearchableStock("ORCL", "Oracle Corporation"),
        SearchableStock("CSCO", "Cisco Systems, Inc."),
        SearchableStock("ADBE", "Adobe Inc."),
        SearchableStock("PYPL", "PayPal Holdings"),
        SearchableStock("UBER", "Uber Technologies"),
        SearchableStock("SQ", "Block, Inc."),
        SearchableStock("SHOP", "Shopify Inc."),
        SearchableStock("SNAP", "Snap Inc."),
        SearchableStock("SPOT", "Spotify Technology")
    )
}