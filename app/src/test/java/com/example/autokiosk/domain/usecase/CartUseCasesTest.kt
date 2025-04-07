package com.example.autokiosk.domain.usecase

import com.example.autokiosk.domain.models.CartItem
import com.example.autokiosk.domain.repository.CartRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CartUseCasesTest {

    private lateinit var cartRepository: CartRepository
    private lateinit var addToCart: AddToCartUseCase
    private lateinit var incrementQuantity: IncrementQuantityUseCase
    private lateinit var decrementQuantity: DecrementQuantityUseCase
    private lateinit var removeFromCart: RemoveFromCartUseCase
    private lateinit var clearCart: ClearCartUseCase
    private lateinit var getCartItems: GetCartItemsUseCase
    private lateinit var getCartItemQuantity: GetCartItemQuantityUseCase

    @Before
    fun setUp() {
        cartRepository = mockk()
        addToCart = AddToCartUseCase(cartRepository)
        incrementQuantity = IncrementQuantityUseCase(cartRepository)
        decrementQuantity = DecrementQuantityUseCase(cartRepository)
        removeFromCart = RemoveFromCartUseCase(cartRepository)
        clearCart = ClearCartUseCase(cartRepository)
        getCartItems = GetCartItemsUseCase(cartRepository)
        getCartItemQuantity = GetCartItemQuantityUseCase(cartRepository)
    }

    @Test
    fun `addToCart calls repository`() = runBlocking {
        coEvery { cartRepository.addToCart("1", 2) } returns Unit

        addToCart.execute("1", 2)

        coVerify { cartRepository.addToCart("1", 2) }
    }

    @Test
    fun `incrementQuantity calls repository and returns new value`() = runBlocking {
        coEvery { cartRepository.incrementQuantity("1") } returns 3

        val result = incrementQuantity.execute("1")

        assertEquals(3, result)
        coVerify { cartRepository.incrementQuantity("1") }
    }

    @Test
    fun `decrementQuantity removes item when quantity is 0`() = runBlocking {
        coEvery { cartRepository.decrementQuantity("1") } returns 0
        coEvery { cartRepository.removeFromCart("1") } returns Unit

        val result = decrementQuantity.execute("1")

        assertEquals(0, result)
        coVerify { cartRepository.removeFromCart("1") }
    }

    @Test
    fun `removeFromCart calls repository`() = runBlocking {
        coEvery { cartRepository.removeFromCart("1") } returns Unit

        removeFromCart.execute("1")

        coVerify { cartRepository.removeFromCart("1") }
    }

    @Test
    fun `clearCart calls repository`() = runBlocking {
        coEvery { cartRepository.clearCart() } returns Unit

        clearCart.execute()

        coVerify { cartRepository.clearCart() }
    }

    @Test
    fun `getCartItems returns flow of cart items`() = runBlocking {
        val items = listOf(
            CartItem("1", "Apple", 10.0, "url", 1),
            CartItem("2", "Orange", 20.0, "url2", 2)
        )
        coEvery { cartRepository.getAllCartItems() } returns flowOf(items)

        val result = getCartItems.execute().first()

        assertEquals(2, result.size)
        assertEquals("Apple", result[0].name)
    }

    @Test
    fun `getCartItemQuantity returns correct quantity`() = runBlocking {
        coEvery { cartRepository.getCartItemQuantity("1") } returns 5

        val result = getCartItemQuantity.execute("1")

        assertEquals(5, result)
    }
}
