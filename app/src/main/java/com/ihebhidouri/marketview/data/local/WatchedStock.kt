package com.ihebhidouri.marketview.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

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