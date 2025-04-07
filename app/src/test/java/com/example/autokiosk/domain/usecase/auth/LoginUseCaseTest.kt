package com.example.autokiosk.domain.usecase.auth

import com.example.autokiosk.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var loginUseCase: LoginUseCase
    private val authRepository: AuthRepository = mockk()

    @Before
    fun setUp() {
        loginUseCase = LoginUseCase(authRepository)
    }

    @Test
    fun `execute returns true when login succeeds`() = runBlocking {
        coEvery { authRepository.loginUser("emem@email.com", "password123") } returns true

        val result = loginUseCase.execute("emem@email.com", "password123")

        assertTrue(result)
        coVerify { authRepository.loginUser("emem@email.com", "password123") }
    }

    @Test
    fun `execute returns false when login fails`() = runBlocking {
        coEvery { authRepository.loginUser("emem@email.com", "wrong") } returns false

        val result = loginUseCase.execute("emem@email.com", "wrong")

        assertFalse(result)
        coVerify { authRepository.loginUser("emem@email.com", "wrong") }
    }
}
