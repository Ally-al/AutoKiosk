package com.example.autokiosk.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.autokiosk.domain.models.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(cartItem: CartItem)

    @Query("UPDATE cart_items SET quantity = quantity + 1 WHERE id = :id")
    suspend fun incrementQuantity(id: String): Int

    @Query("UPDATE cart_items SET quantity = quantity - 1 WHERE id = :id AND quantity > 0")
    suspend fun decrementQuantity(id: String): Int

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun removeFromCart(id: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItem>>

    @Query("SELECT quantity FROM cart_items WHERE id = :id")
    suspend fun getCartItemQuantity(id: String): Int?
}
