package com.example.autokiosk.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.autokiosk.domain.models.Card
import com.example.autokiosk.domain.models.CartItem

@Database(entities = [CartItem::class, Card::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun cardDao(): CardDao
}
