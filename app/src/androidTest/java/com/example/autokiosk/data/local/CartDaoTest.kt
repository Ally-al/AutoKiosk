package com.example.autokiosk.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.autokiosk.domain.models.CartItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var cartDao: CartDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        cartDao = database.cartDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addItemToCart() = runBlocking {
        val cartItem = CartItem(id = "1", name = "Apple", price = 100.0, imageUrl = "url", quantity = 3)
        cartDao.addToCart(cartItem)

        val items = cartDao.getAllCartItems().first()
        assertEquals(1, items.size)
        assertEquals("Apple", items[0].name)
        assertEquals(100.0, items[0].price, 0.0001)
        assertEquals(3, items[0].quantity)
    }

    @Test
    fun incrementCartItemQuantityAndGetQuantity() = runBlocking {
        val cartItem = CartItem(id = "1", name = "Choco", price = 100.0, imageUrl = "url", quantity = 1)
        cartDao.addToCart(cartItem)

        val updatedRows = cartDao.incrementQuantity("1")
        val quantity = cartDao.getCartItemQuantity("1")

        assertEquals(1, updatedRows)
        assertEquals(2, quantity)
    }

    @Test
    fun decrementCartItemQuantityAndGetQuantity() = runBlocking {
        val cartItem = CartItem(id = "3", name = "Melon", price = 100.0, imageUrl = "url", quantity = 5)
        cartDao.addToCart(cartItem)

        val updatedRows = cartDao.decrementQuantity("3")
        val quantity = cartDao.getCartItemQuantity("3")

        assertEquals(1, updatedRows)
        assertEquals(4, quantity)
    }

    @Test
    fun removeItemFromCartAndGetAllCartItems() = runBlocking {
        val cartItem = CartItem(id = "1", name = "Cola", price = 100.0, imageUrl = "url", quantity = 1)
        cartDao.addToCart(cartItem)
        cartDao.removeFromCart("1")

        val items = cartDao.getAllCartItems().first()
        assertEquals(0, items.size)
    }

    @Test
    fun clearCart() = runBlocking {
        val cartItem1 = CartItem(id = "1", name = "Apple", price = 30.0, imageUrl = "url", quantity = 1)
        val cartItem2 = CartItem(id = "2", name = "Banana", price = 50.0, imageUrl = "url", quantity = 1)

        cartDao.addToCart(cartItem1)
        cartDao.addToCart(cartItem2)
        cartDao.clearCart()

        val items = cartDao.getAllCartItems().first()
        assertEquals(0, items.size)
    }

    @Test
    fun getAllCartItemsReturnsAllItems() = runBlocking {
        val cartItem1 = CartItem(id = "1", name = "Milk", price = 40.0, imageUrl = "url", quantity = 2)
        val cartItem2 = CartItem(id = "2", name = "Bread", price = 20.0, imageUrl = "url", quantity = 1)
        val cartItem3 = CartItem(id = "3", name = "Banana", price = 50.0, imageUrl = "url", quantity = 1)

        cartDao.addToCart(cartItem1)
        cartDao.addToCart(cartItem2)
        cartDao.addToCart(cartItem3)

        val items = cartDao.getAllCartItems().first()
        assertEquals(3, items.size)

        assertEquals("Milk", items[0].name)
        assertEquals(40.0, items[0].price, 0.0001)
        assertEquals(2, items[0].quantity)

        assertEquals("Bread", items[1].name)
        assertEquals(20.0, items[1].price, 0.0001)
        assertEquals(1, items[1].quantity)

        assertEquals("Banana", items[2].name)
        assertEquals(50.0, items[2].price, 0.0001)
        assertEquals(1, items[2].quantity)
    }

    @Test
    fun getCartItemQuantityForNonExistingItem() = runBlocking {
        val quantity = cartDao.getCartItemQuantity("999")
        assertEquals(null, quantity)
    }

    @Test
    fun getCartItemQuantityForExistingItem() = runBlocking {
        val cartItem = CartItem(id = "10", name = "Juice", price = 75.0, imageUrl = "url", quantity = 3)
        cartDao.addToCart(cartItem)

        val quantity = cartDao.getCartItemQuantity("10")
        assertEquals(3, quantity)
    }
}
