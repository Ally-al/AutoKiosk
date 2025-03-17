package com.example.autokiosk.presentation.payment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autokiosk.domain.models.Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor() : ViewModel() {

    private val _selectedCard = MutableStateFlow<Card?>(null)
    val selectedCard: StateFlow<Card?> = _selectedCard.asStateFlow()

    private val _paymentStatus = MutableStateFlow<String>("")
    val paymentStatus: StateFlow<String> = _paymentStatus.asStateFlow()

    fun selectCard(card: Card) {
        _selectedCard.value = card
    }

    fun processPayment() {
        _paymentStatus.value = "Оплата в процессе..."

        viewModelScope.launch {
            delay(3000) // Имитация запроса
            val success = (0..1).random() == 1
            _paymentStatus.value = if (success) "Оплата прошла успешно!" else "Ошибка оплаты"
        }
    }
}
