package com.example.autokiosk.di

import com.example.autokiosk.data.repository.ProductRepositoryImpl
import com.example.autokiosk.domain.repository.ProductRepository
import com.example.autokiosk.domain.usecase.products.GetCategoriesUseCase
import com.example.autokiosk.domain.usecase.products.GetProductByIdUseCase
import com.example.autokiosk.domain.usecase.products.GetProductsUseCase
import com.example.autokiosk.domain.usecase.products.ProductUseCases
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppProvideModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideProductUseCases(repository: ProductRepository): ProductUseCases {
        return ProductUseCases(
            getProducts = GetProductsUseCase(repository),
            getProductById = GetProductByIdUseCase(repository),
//            searchProductsByName = SearchProductsByNameUseCase(repository),
//            getProductsByCategory = GetProductsByCategoryUseCase(repository),
            getCategories = GetCategoriesUseCase(repository)
        )
    }
}
