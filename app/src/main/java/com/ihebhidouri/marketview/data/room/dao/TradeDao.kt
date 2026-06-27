package com.ihebhidouri.marketview.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ihebhidouri.marketview.data.room.entity.Trade
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {

    @Query("SELECT * FROM trades WHERE portfolioId = :portfolioId ORDER BY openedAt DESC")
    fun getTradesForPortfolio(portfolioId: Long): Flow<List<Trade>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(trade: Trade)

    @Query("UPDATE trades SET isOpen = 0, exitPrice = :exitPrice, closedAt = :closedAt WHERE id = :tradeId")
    suspend fun closeTrade(tradeId: Long, exitPrice: Double, closedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM trades WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM trades")
    fun getAll(): Flow<List<Trade>>
}