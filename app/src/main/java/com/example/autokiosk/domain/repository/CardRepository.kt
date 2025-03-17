package com.example.autokiosk.domain.repository

import com.example.autokiosk.domain.models.Card
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun getAllCards(): Flow<List<Card>>
    suspend fun addCard(card: Card)
    suspend fun removeCard(cardId: String)
}
