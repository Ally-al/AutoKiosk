package com.example.autokiosk.domain.models

data class Category(
    val name: String = "",
    val subcategories: List<String> = emptyList()
)
