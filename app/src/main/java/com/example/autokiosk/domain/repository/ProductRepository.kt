package com.example.autokiosk.domain.repository

import com.example.autokiosk.domain.models.Category
import com.example.autokiosk.domain.models.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProducts(): Flow<List<Product>>
    suspend fun getProductById(productId: String): Flow<Product?>
    suspend fun getCategories(): Flow<List<Category>>
}
