package com.ihebhidouri.marketview.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "portfolios")
data class Portfolio(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "",
    val ownerName: String = "",
    val name: String,
    val style: String,
    val startingBalance: Double,
    val realizedPnL: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)