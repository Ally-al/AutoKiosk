package com.example.autokiosk.domain.usecase.cart

import com.example.autokiosk.domain.models.CartItem
import com.example.autokiosk.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

data class CartUseCases(
    val addToCart: AddToCartUseCase,
    val incrementQuantity: IncrementQuantityUseCase,
    val decrementQuantity: DecrementQuantityUseCase,
    val removeFromCart: RemoveFromCartUseCase,
    val clearCart: ClearCartUseCase,
    val getCartItems: GetCartItemsUseCase
)

class AddToCartUseCase(private val cartRepository: CartRepository) {
    suspend fun execute(id: String, quantity: Int) {
        cartRepository.addToCart(id, quantity)
    }
}

class IncrementQuantityUseCase(private val cartRepository: CartRepository) {
    suspend fun execute(id: String): Int {
        return cartRepository.incrementQuantity(id)
    }
}

class DecrementQuantityUseCase(private val cartRepository: CartRepository) {
    suspend fun execute(id: String): Int {
        return cartRepository.decrementQuantity(id)
    }
}

class RemoveFromCartUseCase(private val cartRepository: CartRepository) {
    suspend fun execute(id: String) {
        cartRepository.removeFromCart(id)
    }
}

class ClearCartUseCase(private val cartRepository: CartRepository) {
    suspend fun execute() {
        cartRepository.clearCart()
    }
}

class GetCartItemsUseCase(private val cartRepository: CartRepository) {
    fun execute(): Flow<List<CartItem>> {
        return cartRepository.getAllCartItems()
    }
}
