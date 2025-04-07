package com.example.autokiosk.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.autokiosk.domain.models.Card
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var cardDao: CardDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        cardDao = database.cardDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertCardAndGetAllCards() = runBlocking {
        val card = Card(id = "1", lastFourDigits = "1234", expiry = "01/30")
        cardDao.insert(card)

        val cards = cardDao.getAllCards().first() // Получаем Flow первым значением
        assertEquals(1, cards.size)
        assertEquals("1234", cards[0].lastFourDigits)
        assertEquals("01/30", cards[0].expiry)
    }

    @Test
    fun insertMultipleCardsAndGetAll() = runBlocking {
        val card1 = Card(id = "1", lastFourDigits = "1234", expiry = "01/30")
        val card2 = Card(id = "2", lastFourDigits = "5678", expiry = "02/28")
        val card3 = Card(id = "3", lastFourDigits = "9999", expiry = "03/27")

        cardDao.insert(card1)
        cardDao.insert(card2)
        cardDao.insert(card3)

        val cards = cardDao.getAllCards().first()
        assertEquals(3, cards.size)

        assertEquals("1234", cards[0].lastFourDigits)
        assertEquals("01/30", cards[0].expiry)

        assertEquals("5678", cards[1].lastFourDigits)
        assertEquals("02/28", cards[1].expiry)

        assertEquals("9999", cards[2].lastFourDigits)
        assertEquals("03/27", cards[2].expiry)
    }

    @Test
    fun insertDeleteAndGetAllCards() = runBlocking {
        val card = Card(id = "1", lastFourDigits = "1234", expiry = "12/30")
        cardDao.insert(card)
        cardDao.deleteById("1")

        val cards = cardDao.getAllCards().first()
        assertEquals(0, cards.size) // Карта должна быть удалена
    }
}
