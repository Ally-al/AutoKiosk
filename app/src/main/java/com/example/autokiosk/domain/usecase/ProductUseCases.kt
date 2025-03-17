package com.example.autokiosk.domain.usecase

import com.example.autokiosk.domain.models.Category
import com.example.autokiosk.domain.models.Product
import com.example.autokiosk.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

data class ProductUseCases(
    val getProducts: GetProductsUseCase,
    val getProductById: GetProductByIdUseCase,
    val getCategories: GetCategoriesUseCase
)

class GetProductsUseCase(private val productRepository: ProductRepository) {
    suspend fun execute(): Flow<List<Product>> = productRepository.getProducts()
}

class GetProductByIdUseCase(private val productRepository: ProductRepository) {
    suspend fun execute(productId: String): Flow<Product?> = productRepository.getProductById(productId)
}

class GetCategoriesUseCase(private val productRepository: ProductRepository) {
    suspend fun execute(): Flow<List<Category>> = productRepository.getCategories()
}
