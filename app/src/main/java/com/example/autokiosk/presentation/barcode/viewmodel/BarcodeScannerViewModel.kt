package com.example.autokiosk.presentation.barcode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autokiosk.domain.models.Product
import com.example.autokiosk.domain.usecase.ProductUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarcodeScannerViewModel @Inject constructor(
    private val productUseCases: ProductUseCases
) : ViewModel() {

    private val _productFlow = MutableSharedFlow<Product?>()
    val productFlow: SharedFlow<Product?> = _productFlow

    fun searchProductByBarcode(barcode: String) {
        viewModelScope.launch {
            productUseCases.getProductById.execute(barcode).collect { product ->
                _productFlow.emit(product)
            }
        }
    }
}
