package com.ihebhidouri.marketview.data.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ihebhidouri.marketview.data.room.dao.WatchlistDao
import com.ihebhidouri.marketview.data.room.dao.PortfolioDao
import com.ihebhidouri.marketview.data.room.dao.TradeDao
import com.ihebhidouri.marketview.data.room.entity.Portfolio
import com.ihebhidouri.marketview.data.room.entity.Trade
import com.ihebhidouri.marketview.data.room.entity.WatchedStock

@Database(entities = [WatchedStock::class, Portfolio::class, Trade::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
    abstract fun portfolioDao(): PortfolioDao
    abstract fun tradeDao(): TradeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "marketview_database"
                ).fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}