package com.example.autokiosk.data.repository

import com.example.autokiosk.data.local.CardDao
import com.example.autokiosk.domain.models.Card
import com.example.autokiosk.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao
) : CardRepository {

    override fun getAllCards(): Flow<List<Card>> {
        return cardDao.getAllCards()
    }

    override suspend fun addCard(card: Card) {
        cardDao.insert(card)
    }

    override suspend fun removeCard(cardId: String) {
        cardDao.deleteById(cardId)
    }
}
