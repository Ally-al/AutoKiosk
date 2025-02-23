package com.example.autokiosk.domain.models

import com.google.firebase.firestore.PropertyName

data class Product(
    var id: String = "",
    var imageUrls: List<String> = emptyList(),
    var category: List<String> = emptyList(),

    @get:PropertyName("product_name") @set:PropertyName("product_name")
    var productName: String = "",

    @get:PropertyName("product_weight") @set:PropertyName("product_weight")
    var productWeight: String = "",

    @get:PropertyName("product_price") @set:PropertyName("product_price")
    var productPrice: Double = 0.0,

    @get:PropertyName("product_contents") @set:PropertyName("product_contents")
    var productContents: String = "",

    var calories: Double = 0.0,
    var proteins: Double = 0.0,
    var fats: Double = 0.0,
    var carbs: Double = 0.0,

    @get:PropertyName("product_shelf_life") @set:PropertyName("product_shelf_life")
    var productShelfLife: String = "",

    @get:PropertyName("product_brand") @set:PropertyName("product_brand")
    var productBrand: String = ""
)
