package com.ihebhidouri.marketview.data.room.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseSeeder : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val now = System.currentTimeMillis()
            val day = 86400000L

            // User 1 - Marco Rossi (big gains)
            db.execSQL(
                """INSERT INTO portfolios (userId, ownerName, name, style, startingBalance, realizedPnL, createdAt)
                   VALUES ('user_marco', 'Marco Rossi', 'Alpha Fund', 'Aggressive Growth', 50000.0, 14200.0, ${now - 25 * day})"""
            )
            db.execSQL(
                """INSERT INTO trades (portfolioId, symbol, name, type, size, leverage, entryPrice, takeProfit, stopLoss, exitPrice, isOpen, openedAt, closedAt)
                   VALUES (1, 'NVDA', 'NVIDIA Corporation', 'BUY', 15.0, 1.0, 620.0, NULL, NULL, NULL, 1, ${now - 20 * day}, NULL)"""
            )
            db.execSQL(
                """INSERT INTO trades (portfolioId, symbol, name, type, size, leverage, entryPrice, takeProfit, stopLoss, exitPrice, isOpen, openedAt, closedAt)
                   VALUES (1, 'TSLA', 'Tesla, Inc.', 'BUY', 40.0, 1.0, 120.0, NULL, NULL, 165.0, 0, ${now - 22 * day}, ${now - 5 * day})"""
            )
            db.execSQL(
                """INSERT INTO trades (portfolioId, symbol, name, type, size, leverage, entryPrice, takeProfit, stopLoss, exitPrice, isOpen, openedAt, closedAt)
                   VALUES (1, 'META', 'Meta Platforms Inc.', 'BUY', 10.0, 1.0, 380.0, NULL, NULL, 510.0, 0, ${now - 18 * day}, ${now - 3 * day})"""
            )

            // User 2 - Fatma Ben Ali (medium, steady)
            db.execSQL(
                """INSERT INTO portfolios (userId, ownerName, name, style, startingBalance, realizedPnL, createdAt)
                   VALUES ('user_fatma', 'Fatma Ben Ali', 'Balanced Growth', 'Swing Trading', 25000.0, 2800.0, ${now - 20 * day})"""
            )
            db.execSQL(
                """INSERT INTO trades (portfolioId, symbol, name, type, size, leverage, entryPrice, takeProfit, stopLoss, exitPrice, isOpen, openedAt, closedAt)
                   VALUES (2, 'AAPL', 'Apple Inc.', 'BUY', 20.0, 1.0, 185.0, NULL, NULL, NULL, 1, ${now - 14 * day}, NULL)"""
            )
            db.execSQL(
                """INSERT INTO trades (portfolioId, symbol, name, type, size, leverage, entryPrice, takeProfit, stopLoss, exitPrice, isOpen, openedAt, closedAt)
                   VALUES (2, 'V', 'Visa Inc.', 'BUY', 10.0, 1.0, 265.0, NULL, NULL, 282.0, 0, ${now - 18 * day}, ${now - 6 * day})"""
            )

            // User 3 - Youssef Hammami (bad losses)
            db.execSQL(
                """INSERT INTO portfolios (userId, ownerName, name, style, startingBalance, realizedPnL, createdAt)
                   VALUES ('user_youssef', 'Youssef Hammami', 'YOLO Trades', 'High Risk', 50000.0, -9500.0, ${now - 18 * day})"""
            )
            db.execSQL(
                """INSERT INTO trades (portfolioId, symbol, name, type, size, leverage, entryPrice, takeProfit, stopLoss, exitPrice, isOpen, openedAt, closedAt)
                   VALUES (3, 'TSLA', 'Tesla, Inc.', 'BUY', 60.0, 1.0, 210.0, NULL, NULL, NULL, 1, ${now - 10 * day}, NULL)"""
            )
            db.execSQL(
                """INSERT INTO trades (portfolioId, symbol, name, type, size, leverage, entryPrice, takeProfit, stopLoss, exitPrice, isOpen, openedAt, closedAt)
                   VALUES (3, 'SNAP', 'Snap Inc.', 'BUY', 500.0, 1.0, 16.0, NULL, NULL, 11.0, 0, ${now - 15 * day}, ${now - 8 * day})"""
            )
            db.execSQL(
                """INSERT INTO trades (portfolioId, symbol, name, type, size, leverage, entryPrice, takeProfit, stopLoss, exitPrice, isOpen, openedAt, closedAt)
                   VALUES (3, 'NVDA', 'NVIDIA Corporation', 'BUY', 8.0, 1.0, 950.0, NULL, NULL, 870.0, 0, ${now - 12 * day}, ${now - 4 * day})"""
            )
        }
    }
}