package com.ihebhidouri.marketview.models

import com.ihebhidouri.marketview.data.room.entity.Portfolio

data class PortfolioSummary(
    val portfolio: Portfolio,
    val pnlPercent: Double,
    val currentBalance: Double,
    val ownerName: String = ""
)