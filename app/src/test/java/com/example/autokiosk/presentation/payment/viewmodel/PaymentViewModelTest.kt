package com.example.autokiosk.presentation.payment.viewmodel

import app.cash.turbine.test
import com.example.autokiosk.domain.models.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: PaymentViewModel

    private val testCard = Card(
        id = "1",
        lastFourDigits = "2222",
        expiry = "10/27"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PaymentViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `selectCard updates selectedCard state`() = runTest {
        viewModel.selectCard(testCard)

        viewModel.selectedCard.test {
            assertEquals(testCard, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `processPayment sets status to in progress and then success or error`() = runTest {
        viewModel.processPayment()

        viewModel.paymentStatus.test {
            assertEquals("Оплата в процессе...", awaitItem())

            advanceTimeBy(3000)

            val result = awaitItem()
            assert(result == "Оплата прошла успешно!" || result == "Ошибка оплаты")
            cancelAndIgnoreRemainingEvents()
        }
    }
}
