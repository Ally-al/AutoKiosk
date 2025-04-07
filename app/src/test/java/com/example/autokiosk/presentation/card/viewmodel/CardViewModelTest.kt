package com.example.autokiosk.presentation.card.viewmodel

import com.example.autokiosk.domain.models.Card
import com.example.autokiosk.domain.usecase.AddCardUseCase
import com.example.autokiosk.domain.usecase.CardUseCases
import com.example.autokiosk.domain.usecase.GetAllCardsUseCase
import com.example.autokiosk.domain.usecase.RemoveCardUseCase
import com.google.common.base.Verify.verify
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: CardViewModel
    private val getAllCardsUseCase: GetAllCardsUseCase = mockk()
    private val addCardUseCase: AddCardUseCase = mockk()
    private val removeCardUseCase: RemoveCardUseCase = mockk()
    private lateinit var cardUseCases: CardUseCases

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        cardUseCases = CardUseCases(
            getAllCards = getAllCardsUseCase,
            addCard = addCardUseCase,
            removeCard = removeCardUseCase
        )

        every { getAllCardsUseCase.execute() } returns flowOf(emptyList())
        viewModel = CardViewModel(cardUseCases)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addCard should call addCardUseCase and reload cards`() = runTest {
        val cardSlot = slot<Card>()

        coEvery { addCardUseCase.execute(capture(cardSlot)) } just Runs
        every { getAllCardsUseCase.execute() } returns flowOf(listOf(Card("1", "1234", "12/27")))

        viewModel.addCard("1234123412341234", "12/27")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { addCardUseCase.execute(any()) }
        verify { getAllCardsUseCase.execute() }

        val addedCard = cardSlot.captured
        assertEquals("1234", addedCard.lastFourDigits)

        val cards = viewModel.cards.value
        assertEquals(1, cards.size)
        assertEquals("1234", cards[0].lastFourDigits)
    }


    @Test
    fun `removeCard should call removeCardUseCase and reload cards`() = runTest {
        coEvery { removeCardUseCase.execute("1") } just Runs
        every { getAllCardsUseCase.execute() } returns flowOf(emptyList())

        viewModel.removeCard("1")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { removeCardUseCase.execute("1") }
        verify { getAllCardsUseCase.execute() }

        assertTrue(viewModel.cards.value.isEmpty())
    }

    @Test
    fun `loadCards should update cards state`() = runTest {
        val fakeCards = listOf(
            Card("1", "1111", "10/25"),
            Card("2", "2222", "01/26")
        )

        every { getAllCardsUseCase.execute() } returns flowOf(fakeCards)

        viewModel = CardViewModel(cardUseCases)
        testDispatcher.scheduler.advanceUntilIdle()

        verify { getAllCardsUseCase.execute() }

        val cards = viewModel.cards.value
        assertEquals(2, cards.size)
        assertEquals("1111", cards[0].lastFourDigits)
        assertEquals("2222", cards[1].lastFourDigits)
    }
}
