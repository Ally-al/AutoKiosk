package com.example.autokiosk.presentation.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autokiosk.domain.models.CartItem
import com.example.autokiosk.domain.usecase.CartUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartUseCases: CartUseCases
) : ViewModel() {

    val cartItems: StateFlow<List<CartItem>> = cartUseCases.getCartItems.execute()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addToCart(id: String, quantity: Int) {
        viewModelScope.launch {
            cartUseCases.addToCart.execute(id, quantity)
        }
    }

    fun incrementQuantity(id: String) {
        viewModelScope.launch {
            cartUseCases.incrementQuantity.execute(id)
        }
    }

    fun decrementQuantity(id: String) {
        viewModelScope.launch {
            cartUseCases.decrementQuantity.execute(id)
        }
    }

    fun removeFromCart(id: String) {
        viewModelScope.launch {
            cartUseCases.removeFromCart.execute(id)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartUseCases.clearCart.execute()
        }
    }

    fun getCartItemQuantity(id: String): StateFlow<Int> {
        return cartItems
            .map { cart -> cart.find { it.id == id }?.quantity ?: 0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(0), 0)
    }


    fun isProductInCart(id: String): StateFlow<Boolean> {
        return cartItems
            .map { cart -> cart.any { it.id == id } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(0), false)
    }
}
