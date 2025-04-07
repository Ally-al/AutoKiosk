package com.example.autokiosk.data.repository

import com.example.autokiosk.data.local.CardDao
import com.example.autokiosk.domain.models.Card
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CardRepositoryImplTest {

    private lateinit var repository: CardRepositoryImpl
    private val cardDao: CardDao = mockk()

    @Before
    fun setUp() {
        repository = CardRepositoryImpl(cardDao)
    }

    @Test
    fun `getAllCards returns flow of cards`() = runBlocking {
        val cards = listOf(
            Card(id = "1", lastFourDigits = "1234", expiry = "12/30"),
            Card(id = "2", lastFourDigits = "5678", expiry = "11/29")
        )

        every { cardDao.getAllCards() } returns flowOf(cards)

        val result = repository.getAllCards().first()
        assertEquals(2, result.size)
        assertEquals("1234", result[0].lastFourDigits)
    }

    @Test
    fun `addCard calls insert on dao`() = runBlocking {
        val card = Card(id = "1", lastFourDigits = "9999", expiry = "10/28")
        coEvery { cardDao.insert(card) } just Runs

        repository.addCard(card)

        coVerify { cardDao.insert(card) }
    }

    @Test
    fun `removeCard calls deleteById on dao`() = runBlocking {
        coEvery { cardDao.deleteById("1") } just Runs

        repository.removeCard("1")

        coVerify { cardDao.deleteById("1") }
    }
}
