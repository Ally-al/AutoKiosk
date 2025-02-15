package com.example.autokiosk.domain.usecase.auth

import com.example.autokiosk.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String, password: String): Boolean {
        return authRepository.registerUser(email, password)
    }
}
