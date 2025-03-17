package com.example.autokiosk.presentation.catalog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autokiosk.domain.models.Category
import com.example.autokiosk.domain.models.Product
import com.example.autokiosk.domain.usecase.ProductUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val productUseCases: ProductUseCases
) : ViewModel() {

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private var currentQuery: String = ""
    private var chosenFromMenu: String? = null

    init {
        loadProducts()
        loadCategories()
    }

    fun loadProducts() {
        viewModelScope.launch {
            productUseCases.getProducts.execute()
                .catch { _allProducts.value = emptyList() }
                .collect { products ->
                    _allProducts.value = products
                    applyFilters()
                }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            productUseCases.getCategories.execute()
                .catch { _categories.value = emptyList() }
                .collect { categoryList ->
                    _categories.value = categoryList
                }
        }
    }

    fun searchProducts(query: String) {
        currentQuery = query.lowercase()
        applyFilters()
    }

    fun filterProductsByCategory(selectedCategory: String?) {
        chosenFromMenu = selectedCategory
        applyFilters()
    }

    private fun applyFilters() {
        val filtered = _allProducts.value.filter { product ->
            val matchesQuery = product.productName.lowercase().contains(currentQuery)
            val matchesCategory = chosenFromMenu == null || product.category.any { it == chosenFromMenu }
            matchesQuery && matchesCategory
        }
        _products.value = filtered
    }

    fun resetCategoryFilter() {
        chosenFromMenu = null
        applyFilters()
    }
}
