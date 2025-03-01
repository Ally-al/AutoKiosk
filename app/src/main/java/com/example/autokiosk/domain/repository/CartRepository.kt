package com.example.autokiosk.domain.repository

import com.example.autokiosk.domain.models.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun addToCart(id: String, quantity: Int)
    suspend fun incrementQuantity(id: String): Int
    suspend fun decrementQuantity(id: String): Int
    suspend fun removeFromCart(id: String)
    suspend fun clearCart()
    fun getAllCartItems(): Flow<List<CartItem>>
}
