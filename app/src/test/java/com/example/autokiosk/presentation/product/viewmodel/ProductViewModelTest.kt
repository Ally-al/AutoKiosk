package com.example.autokiosk.presentation.product.viewmodel

import app.cash.turbine.test
import com.example.autokiosk.domain.models.Product
import com.example.autokiosk.domain.usecase.GetProductByIdUseCase
import com.example.autokiosk.domain.usecase.ProductUseCases
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ProductViewModel
    private lateinit var getProductByIdUseCase: GetProductByIdUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getProductByIdUseCase = mockk()
        val productUseCases = ProductUseCases(
            getProducts = mockk(),
            getProductById = getProductByIdUseCase,
            getCategories = mockk()
        )

        viewModel = ProductViewModel(productUseCases)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProduct sets product value on success`() = runTest {
        val sampleProduct = Product(
            id = "2",
            productName = "Chicken",
            category = listOf("Kuritsa", "Bird")
        )

        coEvery { getProductByIdUseCase.execute("2") } returns flow { emit(sampleProduct) }

        viewModel.loadProduct("2")
        advanceUntilIdle()

        viewModel.product.test {
            val result = awaitItem()
            assertEquals(sampleProduct, result)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadProduct sets empty product on failure`() = runTest {
        coEvery { getProductByIdUseCase.execute("9") } returns flow {
            throw RuntimeException("Not found")
        }

        viewModel.loadProduct("9")
        advanceUntilIdle()

        viewModel.product.test {
            val result = awaitItem()
            assertEquals(Product(), result)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
