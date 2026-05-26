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
        SearchableStock("SPOT", "Spotify Technology"),
        SearchableStock("DIS", "Walt Disney Co."),
        SearchableStock("V", "Visa Inc."),
        SearchableStock("MA", "Mastercard Inc."),
        SearchableStock("JPM", "JPMorgan Chase & Co."),
        SearchableStock("BAC", "Bank of America Corp."),
        SearchableStock("GS", "Goldman Sachs Group"),
        SearchableStock("WMT", "Walmart Inc."),
        SearchableStock("KO", "Coca-Cola Co."),
        SearchableStock("PEP", "PepsiCo Inc."),
        SearchableStock("MCD", "McDonald's Corp."),
        SearchableStock("NKE", "Nike Inc."),
        SearchableStock("JNJ", "Johnson & Johnson"),
        SearchableStock("PFE", "Pfizer Inc."),
        SearchableStock("XOM", "Exxon Mobil Corp."),
        SearchableStock("CVX", "Chevron Corp."),
        SearchableStock("BA", "Boeing Co."),
        SearchableStock("COST", "Costco Wholesale"),
        SearchableStock("HD", "Home Depot Inc."),
        SearchableStock("LLY", "Eli Lilly & Co."),
        SearchableStock("ABNB", "Airbnb Inc."),
        SearchableStock("COIN", "Coinbase Global"),
        SearchableStock("PLTR", "Palantir Technologies"),
        SearchableStock("RIVN", "Rivian Automotive"),
        SearchableStock("SOFI", "SoFi Technologies")
    )

}