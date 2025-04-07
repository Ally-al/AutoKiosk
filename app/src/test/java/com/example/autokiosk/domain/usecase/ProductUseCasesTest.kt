package com.example.autokiosk.domain.usecase

import com.example.autokiosk.domain.models.Category
import com.example.autokiosk.domain.models.Product
import com.example.autokiosk.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProductUseCasesTest {

    private lateinit var productRepository: ProductRepository
    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var getProductByIdUseCase: GetProductByIdUseCase
    private lateinit var getCategoriesUseCase: GetCategoriesUseCase

    @Before
    fun setUp() {
        productRepository = mockk()
        getProductsUseCase = GetProductsUseCase(productRepository)
        getProductByIdUseCase = GetProductByIdUseCase(productRepository)
        getCategoriesUseCase = GetCategoriesUseCase(productRepository)
    }

    @Test
    fun `getProducts returns list of products`() = runBlocking {
        val products = listOf(
            Product(id = "1", productName = "Cola", productPrice = 100.0),
            Product(id = "2", productName = "Water", productPrice = 15.0)
        )
        coEvery { productRepository.getProducts() } returns flowOf(products)

        val result = getProductsUseCase.execute().first()

        assertEquals(2, result.size)
        assertEquals("Cola", result[0].productName)
    }

    @Test
    fun `getProductById returns correct product`() = runBlocking {
        val product = Product(id = "2", productName = "Banana", productPrice = 50.0)
        coEvery { productRepository.getProductById("2") } returns flowOf(product)

        val result = getProductByIdUseCase.execute("2").first()

        assertEquals("Banana", result?.productName)
    }

    @Test
    fun `getCategories returns list of categories`() = runBlocking {
        val categories = listOf(
            Category(name = "Drinks", subcategories = listOf("Juice", "Water"))
        )
        coEvery { productRepository.getCategories() } returns flowOf(categories)

        val result = getCategoriesUseCase.execute().first()

        assertEquals("Drinks", result[0].name)
        assertEquals(listOf("Juice", "Water"), result[0].subcategories)
    }
}
