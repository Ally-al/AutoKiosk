package com.example.autokiosk.presentation.catalog.viewmodel

import app.cash.turbine.test
import com.example.autokiosk.domain.models.Category
import com.example.autokiosk.domain.models.Product
import com.example.autokiosk.domain.usecase.GetCategoriesUseCase
import com.example.autokiosk.domain.usecase.GetProductByIdUseCase
import com.example.autokiosk.domain.usecase.GetProductsUseCase
import com.example.autokiosk.domain.usecase.ProductUseCases
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: CatalogViewModel
    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var getProductByIdUseCase: GetProductByIdUseCase
    private lateinit var getCategoriesUseCase: GetCategoriesUseCase

    private val sampleProducts = listOf(
        Product(id = "1", productName = "Milk", category = listOf("Dairy")),
        Product(id = "2", productName = "Bread", category = listOf("Bakery")),
        Product(id = "3", productName = "Yogurt", category = listOf("Dairy")),
    )

    private val sampleCategories = listOf(
        Category(name = "Dairy"),
        Category(name = "Bakery"),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getProductsUseCase = mockk(relaxed = true)
        getProductByIdUseCase = mockk()
        getCategoriesUseCase = mockk(relaxed = true)

        coEvery { getProductsUseCase.execute() } returns flow { emit(sampleProducts) }
        coEvery { getCategoriesUseCase.execute() } returns flow { emit(sampleCategories) }

        val productUseCases = ProductUseCases(
            getProducts = getProductsUseCase,
            getProductById = getProductByIdUseCase,
            getCategories = getCategoriesUseCase
        )

        viewModel = CatalogViewModel(productUseCases)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProducts loads all products list`() = runTest {
        viewModel.products.test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals("Milk", result[0].productName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchProducts filters products by query`() = runTest {
        viewModel.searchProducts("bread")
        viewModel.products.test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Bread", result[0].productName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filterProductsByCategory filters products by category`() = runTest {
        viewModel.filterProductsByCategory("Dairy")
        viewModel.products.test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.all { it.category.contains("Dairy")})
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchProducts and filterProductsByCategory both apply`() = runTest {
        viewModel.searchProducts("yogurt")
        viewModel.filterProductsByCategory("Dairy")
        viewModel.products.test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Yogurt", result[0].productName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `resetCategoryFilter removes category filtering`() = runTest {
        viewModel.filterProductsByCategory("Dairy")
        viewModel.resetCategoryFilter()
        viewModel.products.test {
            val result = awaitItem()
            assertEquals(3, result.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadCategories loads category list`() = runTest {
        viewModel.categories.test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Dairy", result[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
