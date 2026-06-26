package com.ihebhidouri.marketview.models

import com.ihebhidouri.marketview.data.local.Portfolio

data class PortfolioSummary(
    val portfolio: Portfolio,
    val pnlPercent: Double,
    val currentBalance: Double,
    val ownerName: String = ""
)