package com.example.autokiosk.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.autokiosk.domain.models.CartItem

@Database(entities = [CartItem::class], version = 1, exportSchema = false)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
