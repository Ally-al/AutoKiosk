package com.example.autokiosk.data.repository

import com.example.autokiosk.domain.models.Category
import com.example.autokiosk.domain.models.Product
import com.example.autokiosk.domain.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductRepository {

    private val productCollection = firestore.collection("products")

    override suspend fun getProducts(): Flow<List<Product>> = flow {
        val productList = productCollection.get().await().documents.map { document ->
            document.toObject(Product::class.java) ?: Product()
        }
        emit(productList)
    }

    override suspend fun getProductById(productId: String): Flow<Product?> = flow {
        val product = productCollection.document(productId).get().await().toObject(Product::class.java)

        if (product == null) {
            emit(null)
        } else {
            emit(product)
        }
    }

    override suspend fun getCategories(): Flow<List<Category>> = flow {
        val categoryList = firestore.collection("categories")
            .get()
            .await()
            .documents
            .map { document ->
                Category(
                    name = document.getString("name") ?: "",
                    subcategories = document.get("subcategories") as? List<String> ?: emptyList()
                )
            }
        emit(categoryList)
    }
}
