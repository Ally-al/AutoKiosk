package com.example.autokiosk.presentation.barcode.viewmodel

import app.cash.turbine.test
import com.example.autokiosk.domain.models.Product
import com.example.autokiosk.domain.usecase.ProductUseCases
import com.example.autokiosk.domain.usecase.GetProductByIdUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BarcodeScannerViewModelTest {

    private lateinit var viewModel: BarcodeScannerViewModel
    private val getProductByIdUseCase: GetProductByIdUseCase = mockk()
    private val productUseCases = ProductUseCases(
        getProducts = mockk(),
        getProductById = getProductByIdUseCase,
        getCategories = mockk()
    )

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = BarcodeScannerViewModel(productUseCases)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchProductByBarcode emits found product`() = runTest {
        val product = Product(id = "1", productName = "Watermelon", productPrice = 300.0)
        coEvery { getProductByIdUseCase.execute("1") } returns flowOf(product)

        viewModel.productFlow.test {
            viewModel.searchProductByBarcode("1")
            testDispatcher.scheduler.advanceUntilIdle()

            val result = awaitItem()
            assertEquals(product, result)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchProductByBarcode emits null if not found`() = runTest {
        coEvery { getProductByIdUseCase.execute("0") } returns flowOf(null)

        viewModel.productFlow.test {
            viewModel.searchProductByBarcode("0")
            testDispatcher.scheduler.advanceUntilIdle()

            val result = awaitItem()
            assertEquals(null, result)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
