package com.example.autokiosk.domain.usecase

import com.example.autokiosk.domain.models.Card
import com.example.autokiosk.domain.repository.CardRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CardUseCasesTest {

    private lateinit var repository: CardRepository
    private lateinit var getAllCardsUseCase: GetAllCardsUseCase
    private lateinit var addCardUseCase: AddCardUseCase
    private lateinit var removeCardUseCase: RemoveCardUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getAllCardsUseCase = GetAllCardsUseCase(repository)
        addCardUseCase = AddCardUseCase(repository)
        removeCardUseCase = RemoveCardUseCase(repository)
    }

    @Test
    fun `getAllCards returns correct list`() = runBlocking {
        val cards = listOf(
            Card(id = "1", lastFourDigits = "1234", expiry = "12/30"),
            Card(id = "2", lastFourDigits = "5678", expiry = "11/29")
        )
        every { repository.getAllCards() } returns flowOf(cards)

        val result = getAllCardsUseCase.execute().first()

        assertEquals(2, result.size)
        assertEquals("5678", result[1].lastFourDigits)
    }

    @Test
    fun `addCard calls repository correctly`() = runBlocking {
        val card = Card(id = "3", lastFourDigits = "9999", expiry = "07/28")
        coEvery { repository.addCard(card) } returns Unit

        addCardUseCase.execute(card)

        coVerify { repository.addCard(card) }
    }

    @Test
    fun `removeCard calls repository correctly`() = runBlocking {
        coEvery { repository.removeCard("1") } returns Unit

        removeCardUseCase.execute("1")

        coVerify { repository.removeCard("1") }
    }
}
