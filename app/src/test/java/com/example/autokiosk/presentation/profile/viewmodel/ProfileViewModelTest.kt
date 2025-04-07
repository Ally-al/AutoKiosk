package com.example.autokiosk.presentation.profile.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.autokiosk.domain.models.Card
import com.example.autokiosk.domain.usecase.CardUseCases
import com.example.autokiosk.domain.usecase.GetAllCardsUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var getAllCardsUseCase: GetAllCardsUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        firebaseAuth = mockk(relaxed = true)
        firebaseUser = mockk()
        getAllCardsUseCase = mockk()

        val cardUseCases = CardUseCases(
            getAllCards = getAllCardsUseCase,
            addCard = mockk(),
            removeCard = mockk()
        )

        every { firebaseAuth.currentUser } returns firebaseUser
        every { firebaseUser.email } returns "emem@email.com"
        every { firebaseUser.uid } returns "12345"

        coEvery { getAllCardsUseCase.execute() } returns flowOf(listOf(Card("1", "1234", "10/30")))

        viewModel = ProfileViewModel(firebaseAuth, cardUseCases)

        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserEmail sets correct email`() {
        viewModel.loadUserEmail()
        assertEquals("emem@email.com", viewModel.email.value)
    }

    @Test
    fun `loadUserEmail sets fallback email when not logged in`() {
        every { firebaseAuth.currentUser } returns null
        viewModel.loadUserEmail()
        assertEquals("Не авторизован", viewModel.email.value)
    }

    @Test
    fun `getUserId returns correct uid`() {
        assertEquals("12345", viewModel.getUserId())
    }

    @Test
    fun `logout calls signOut`() {
        viewModel.logout()
        verify { firebaseAuth.signOut() }
    }

    @Test
    fun `hasLinkedCards is true when cards are present`() = runTest {
        assertTrue(viewModel.hasLinkedCards.value)
    }

    @Test
    fun `hasLinkedCards is false when no cards`() = runTest {
        coEvery { getAllCardsUseCase.execute() } returns flowOf(emptyList())

        val viewModel2 = ProfileViewModel(firebaseAuth, CardUseCases(
            getAllCards = getAllCardsUseCase,
            addCard = mockk(),
            removeCard = mockk()
        ))

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel2.hasLinkedCards.value)
    }
}
