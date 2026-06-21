package com.ihebhidouri.marketview.models

import com.ihebhidouri.marketview.data.local.Trade

class PnLCalculator {

    fun calculate(trade: Trade, currentPrice: Double): Double {
        val price = if (trade.isOpen) currentPrice else (trade.exitPrice ?: trade.entryPrice)
        return when (trade.type) {
            "BUY" -> (price - trade.entryPrice) * trade.size * trade.leverage
            "SELL" -> (trade.entryPrice - price) * trade.size * trade.leverage
            else -> 0.0
        }
    }
}