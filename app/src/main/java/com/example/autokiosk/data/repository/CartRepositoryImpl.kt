package com.example.autokiosk.data.repository

import com.example.autokiosk.data.local.CartDao
import com.example.autokiosk.domain.models.CartItem
import com.example.autokiosk.domain.repository.CartRepository
import com.example.autokiosk.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val productRepository: ProductRepository
) : CartRepository {

    override suspend fun addToCart(id: String, quantity: Int) {
        val product = productRepository.getProductById(id)
            .firstOrNull() ?: return

        val cartItem = CartItem(
            id = product.id,
            name = product.productName,
            price = product.productPrice,
            imageUrl = product.imageUrls.firstOrNull() ?: "",
            quantity = quantity
        )

        cartDao.addToCart(cartItem)
    }


    override suspend fun incrementQuantity(id: String): Int {
        return cartDao.incrementQuantity(id)
    }

    override suspend fun decrementQuantity(id: String): Int {
        return cartDao.decrementQuantity(id)
    }

    override suspend fun removeFromCart(id: String) {
        cartDao.removeFromCart(id)
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }

    override fun getAllCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems()
    }
}
