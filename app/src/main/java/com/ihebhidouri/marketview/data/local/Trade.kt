package com.ihebhidouri.marketview.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trades",
    foreignKeys = [
        ForeignKey(
            entity = Portfolio::class,
            parentColumns = ["id"],
            childColumns = ["portfolioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("portfolioId")]
)
data class Trade(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val portfolioId: Long,
    val symbol: String,
    val name: String,
    val type: String,
    val size: Double,
    val leverage: Double,
    val entryPrice: Double,
    val takeProfit: Double?,
    val stopLoss: Double?,
    val exitPrice: Double?,
    val isOpen: Boolean = true,
    val openedAt: Long = System.currentTimeMillis(),
    val closedAt: Long? = null
)