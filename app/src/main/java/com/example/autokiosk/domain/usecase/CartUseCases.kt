package com.example.autokiosk.domain.usecase

import com.example.autokiosk.domain.models.CartItem
import com.example.autokiosk.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

data class CartUseCases(
    val addToCart: AddToCartUseCase,
    val incrementQuantity: IncrementQuantityUseCase,
    val decrementQuantity: DecrementQuantityUseCase,
    val removeFromCart: RemoveFromCartUseCase,
    val clearCart: ClearCartUseCase,
    val getCartItems: GetCartItemsUseCase,
    val getCartItemQuantity: GetCartItemQuantityUseCase
)

class AddToCartUseCase(private val cartRepository: CartRepository) {
    suspend fun execute(id: String, quantity: Int) = cartRepository.addToCart(id, quantity)
}

class IncrementQuantityUseCase(private val cartRepository: CartRepository) {
    suspend fun execute(id: String): Int = cartRepository.incrementQuantity(id)
}

class DecrementQuantityUseCase(private val cartRepository: CartRepository) {
    suspend fun execute(id: String): Int {
        val newQuantity = cartRepository.decrementQuantity(id)
        if (newQuantity == 0) {
            cartRepository.removeFromCart(id)
        }
        return newQuantity
    }
}


class RemoveFromCartUseCase(private val cartRepository: CartRepository) {
    suspend fun execute(id: String) = cartRepository.removeFromCart(id)
}

class ClearCartUseCase(private val cartRepository: CartRepository) {
    suspend fun execute() = cartRepository.clearCart()
}

class GetCartItemsUseCase(private val cartRepository: CartRepository) {
    fun execute(): Flow<List<CartItem>> = cartRepository.getAllCartItems()
}

class GetCartItemQuantityUseCase(private val cartRepository: CartRepository) {
    suspend fun execute(id: String): Int = cartRepository.getCartItemQuantity(id) ?: 0
}
