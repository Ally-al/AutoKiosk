package com.example.autokiosk.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    var quantity: Int
)
