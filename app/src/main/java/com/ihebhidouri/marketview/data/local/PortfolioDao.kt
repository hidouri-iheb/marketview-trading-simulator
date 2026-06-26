package com.ihebhidouri.marketview.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {

    @Query("SELECT * FROM portfolios WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAll(userId: String): Flow<List<Portfolio>>

    @Query("SELECT * FROM portfolios ORDER BY createdAt DESC")
    fun getAllGlobal(): Flow<List<Portfolio>>
    @Query("SELECT * FROM portfolios WHERE id = :id")
    suspend fun getById(id: Long): Portfolio?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(portfolio: Portfolio): Long

    @Query("DELETE FROM portfolios WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE portfolios SET realizedPnL = realizedPnL + :pnl WHERE id = :id")
    suspend fun addRealizedPnL(id: Long, pnl: Double)
}