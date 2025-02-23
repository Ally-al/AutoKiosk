package com.example.autokiosk.domain.usecase.products

import com.example.autokiosk.domain.models.Category
import com.example.autokiosk.domain.models.Product
import com.example.autokiosk.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

data class ProductUseCases(
    val getProducts: GetProductsUseCase,
    val getProductById: GetProductByIdUseCase,
//    val searchProductsByName: SearchProductsByNameUseCase,
//    val getProductsByCategory: GetProductsByCategoryUseCase,
    val getCategories: GetCategoriesUseCase
)

class GetProductsUseCase(private val productRepository: ProductRepository) {
    suspend fun execute(): Flow<List<Product>> = productRepository.getProducts()
}

class GetProductByIdUseCase(private val productRepository: ProductRepository) {
    suspend fun execute(productId: String): Flow<Product> = productRepository.getProductById(productId)
}

//class SearchProductsByNameUseCase(private val productRepository: ProductRepository) {
//    suspend fun execute(query: String): Flow<List<Product>> = productRepository.searchProductsByName(query)
//}
//
//class GetProductsByCategoryUseCase(private val productRepository: ProductRepository) {
//    suspend fun execute(category: String): Flow<List<Product>> = productRepository.getProductsByCategory(category)
//}

class GetCategoriesUseCase(private val productRepository: ProductRepository) {
    suspend fun execute(): Flow<List<Category>> = productRepository.getCategories()
}

