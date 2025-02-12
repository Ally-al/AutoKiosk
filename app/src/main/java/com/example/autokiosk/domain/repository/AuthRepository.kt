package com.example.autokiosk.domain.repository

interface AuthRepository {
    suspend fun registerUser(email: String, password: String): Boolean
    suspend fun loginUser(email: String, password: String): Boolean
}
