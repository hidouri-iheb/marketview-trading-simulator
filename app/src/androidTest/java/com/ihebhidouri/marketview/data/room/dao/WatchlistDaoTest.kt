package com.ihebhidouri.marketview.data.room.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ihebhidouri.marketview.data.room.database.AppDatabase
import com.ihebhidouri.marketview.data.room.entity.WatchedStock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WatchlistDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: WatchlistDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        dao = database.watchlistDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertStock_stockAppearsInWatchlist() = runTest {
        val stock = WatchedStock(
            symbol = "AAPL",
            userId = "user123",
            name = "Apple Inc.",
            exchange = "NASDAQ",
            currency = "USD",
            basePrice = 200.0
        )

        dao.insert(stock)

        val watchlist = dao.getAll("user123").first()

        Assert.assertEquals(1, watchlist.size)
        Assert.assertEquals("AAPL", watchlist[0].symbol)
    }
}