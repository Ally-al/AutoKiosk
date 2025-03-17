package com.example.autokiosk.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS cards (
                id TEXT PRIMARY KEY NOT NULL,
                lastFourDigits TEXT NOT NULL,
                expiry TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}
