package com.example.autokiosk.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey val id: String,
    val lastFourDigits: String,
    val expiry: String
)
