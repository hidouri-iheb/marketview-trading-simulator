package com.ihebhidouri.marketview.data.room.entity

import androidx.room.Entity

@Entity(tableName = "watched_stocks", primaryKeys = ["symbol", "userId"])
data class WatchedStock(
    val symbol: String,
    val userId: String,
    val name: String,
    val exchange: String,
    val currency: String,
    val basePrice: Double,
    val addedAt: Long = System.currentTimeMillis()
)