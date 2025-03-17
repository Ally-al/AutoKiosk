package com.example.autokiosk.presentation.card.viewmodel

import androidx.lifecycle.*
import com.example.autokiosk.domain.models.Card
import com.example.autokiosk.domain.usecase.CardUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
    private val cardUseCases: CardUseCases
) : ViewModel() {

    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards.asStateFlow()

    init {
        loadCards()
    }

    private fun loadCards() {
        viewModelScope.launch {
            cardUseCases.getAllCards.execute()
                .catch { _ -> _cards.value = emptyList() }
                .collect {
                    cardList -> _cards.value = cardList
                }
        }
    }

    fun addCard(cardNumber: String, expiry: String) {
        val lastFour = cardNumber.takeLast(4)
        val newCard = Card(
            id = UUID.randomUUID().toString(),
            lastFourDigits = lastFour,
            expiry = expiry
        )

        viewModelScope.launch {
            cardUseCases.addCard.execute(newCard)
            loadCards()
        }
    }

    fun removeCard(cardId: String) {
        viewModelScope.launch {
            cardUseCases.removeCard.execute(cardId)
            loadCards()
        }
    }
}
