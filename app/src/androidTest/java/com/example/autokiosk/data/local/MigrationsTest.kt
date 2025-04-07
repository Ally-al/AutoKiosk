package com.example.autokiosk.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationsTest {

    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        database = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java, "test_db"
        )
            .addMigrations(MIGRATION_2_3)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun migrateFromVersion2To3_createsCardsTable() {
        val db = Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java, "test_db"
        )
            .addMigrations(MIGRATION_2_3)
            .build()

        db.openHelper.writableDatabase.apply {
            execSQL("INSERT INTO cards (id, lastFourDigits, expiry) VALUES ('1', '1234', '12/30')")
            close()
        }

        val migratedDb = database.openHelper.readableDatabase
        val cursor = migratedDb.query("SELECT * FROM cards")
        cursor.moveToFirst()

        assertEquals("1", cursor.getString(cursor.getColumnIndexOrThrow("id")))
        assertEquals("1234", cursor.getString(cursor.getColumnIndexOrThrow("lastFourDigits")))
        assertEquals("12/30", cursor.getString(cursor.getColumnIndexOrThrow("expiry")))

        cursor.close()
    }
}
