package com.example.autokiosk.domain.usecase.auth

import com.example.autokiosk.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String, password: String): Boolean {
        return authRepository.loginUser(email, password)
    }
}
