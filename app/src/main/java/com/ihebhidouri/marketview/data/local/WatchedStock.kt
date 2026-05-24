package com.ihebhidouri.marketview.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watched_stocks")
data class WatchedStock(
    @PrimaryKey val symbol: String,
    val name: String,
    val exchange: String,
    val currency: String,
    val basePrice: Double,
    val addedAt: Long = System.currentTimeMillis()
)