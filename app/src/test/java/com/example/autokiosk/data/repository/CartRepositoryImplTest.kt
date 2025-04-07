package com.example.autokiosk.data.repository

import com.example.autokiosk.data.local.CartDao
import com.example.autokiosk.domain.models.CartItem
import com.example.autokiosk.domain.models.Product
import com.example.autokiosk.domain.repository.ProductRepository
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CartRepositoryImplTest {

    private lateinit var repository: CartRepositoryImpl
    private val cartDao: CartDao = mockk(relaxed = true)
    private val productRepository: ProductRepository = mockk()

    @Before
    fun setUp() {
        repository = CartRepositoryImpl(cartDao, productRepository)
    }

    @Test
    fun `addToCart adds product to dao`() = runBlocking {
        val product = Product(
            id = "1",
            productName = "Apple",
            productPrice = 10.0,
            imageUrls = listOf("url")
        )

        coEvery { productRepository.getProductById("1") } returns flowOf(product)
        coEvery { cartDao.addToCart(any()) } just Runs

        repository.addToCart("1", 3)

        coVerify {
            cartDao.addToCart(
                CartItem(
                    id = "1",
                    name = "Apple",
                    price = 10.0,
                    imageUrl = "url",
                    quantity = 3
                )
            )
        }
    }

    @Test
    fun `incrementQuantity calls dao`() = runBlocking {
        coEvery { cartDao.incrementQuantity("1") } returns 1

        val result = repository.incrementQuantity("1")

        assertEquals(1, result)
        coVerify { cartDao.incrementQuantity("1") }
    }

    @Test
    fun `decrementQuantity calls dao and returns quantity`() = runBlocking {
        coEvery { cartDao.decrementQuantity("1") } returns 1
        coEvery { cartDao.getCartItemQuantity("1") } returns 2

        val result = repository.decrementQuantity("1")

        assertEquals(2, result)
        coVerify { cartDao.decrementQuantity("1") }
        coVerify { cartDao.getCartItemQuantity("1") }
    }

    @Test
    fun `removeFromCart calls dao`() = runBlocking {
        coEvery { cartDao.removeFromCart("1") } just Runs

        repository.removeFromCart("1")

        coVerify { cartDao.removeFromCart("1") }
    }

    @Test
    fun `clearCart calls dao`() = runBlocking {
        coEvery { cartDao.clearCart() } just Runs

        repository.clearCart()

        coVerify { cartDao.clearCart() }
    }

    @Test
    fun `getAllCartItems returns flow from dao`() = runBlocking {
        val items = listOf(
            CartItem(id = "1", name = "BottleOfWater", price = 100.0, imageUrl = "url", quantity = 1),
            CartItem(id = "2", name = "Banana", price = 150.0, imageUrl = "url1", quantity = 2)
        )

        every { cartDao.getAllCartItems() } returns flowOf(items)

        val result = repository.getAllCartItems().first()

        assertEquals(2, result.size)
        assertEquals("BottleOfWater", result[0].name)
    }

    @Test
    fun `getCartItemQuantity returns correct value`() = runBlocking {
        coEvery { cartDao.getCartItemQuantity("1") } returns 5

        val result = repository.getCartItemQuantity("1")

        assertEquals(5, result)
        coVerify { cartDao.getCartItemQuantity("1") }
    }
}
