package com.ihebhidouri.marketview.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watched_stocks ORDER BY addedAt DESC")
    fun getAll(): Flow<List<WatchedStock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stock: WatchedStock)

    @Delete
    suspend fun delete(stock: WatchedStock)
}