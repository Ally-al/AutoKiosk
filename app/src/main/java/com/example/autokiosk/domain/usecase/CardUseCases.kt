package com.example.autokiosk.domain.usecase

import com.example.autokiosk.domain.models.Card
import com.example.autokiosk.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow

data class CardUseCases(
    val getAllCards: GetAllCardsUseCase,
    val addCard: AddCardUseCase,
    val removeCard: RemoveCardUseCase
)

class GetAllCardsUseCase(private val repository: CardRepository) {
    fun execute(): Flow<List<Card>> = repository.getAllCards()
}

class AddCardUseCase(private val repository: CardRepository) {
    suspend fun execute(card: Card) = repository.addCard(card)
}

class RemoveCardUseCase(private val repository: CardRepository) {
    suspend fun execute(cardId: String) = repository.removeCard(cardId)
}
