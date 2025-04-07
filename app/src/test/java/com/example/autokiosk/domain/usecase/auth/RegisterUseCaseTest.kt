package com.example.autokiosk.domain.usecase.auth

import com.example.autokiosk.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RegisterUseCaseTest {

    private lateinit var registerUseCase: RegisterUseCase
    private val authRepository: AuthRepository = mockk()

    @Before
    fun setUp() {
        registerUseCase = RegisterUseCase(authRepository)
    }

    @Test
    fun `execute returns true when registration succeeds`() = runBlocking {
        coEvery { authRepository.registerUser(any(), any()) } returns true

        val result = registerUseCase.execute("emem@email.com", "password123")

        assertTrue(result)
    }

    @Test
    fun `execute returns false when registration fails`() = runBlocking {
        coEvery { authRepository.registerUser(any(), any()) } returns false

        val result = registerUseCase.execute("emem@email.com", "password123")

        assertFalse(result)
    }
}
