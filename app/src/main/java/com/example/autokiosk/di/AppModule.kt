package com.example.autokiosk.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.autokiosk.data.local.CartDao
import com.example.autokiosk.data.local.CartDatabase
import com.example.autokiosk.data.repository.CartRepositoryImpl
import com.example.autokiosk.data.repository.ProductRepositoryImpl
import com.example.autokiosk.domain.repository.CartRepository
import com.example.autokiosk.domain.repository.ProductRepository
import com.example.autokiosk.domain.usecase.cart.*
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

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppProvideModule {
    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideProductUseCases(repository: ProductRepository): ProductUseCases {
        return ProductUseCases(
            getProducts = GetProductsUseCase(repository),
            getProductById = GetProductByIdUseCase(repository),
            getCategories = GetCategoriesUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideCartDatabase(context: Context): CartDatabase {
        return Room.databaseBuilder(
            context,
            CartDatabase::class.java,
            "cart_db"
        ).build()
    }

    @Provides
    fun provideCartDao(cartDatabase: CartDatabase): CartDao {
        return cartDatabase.cartDao()
    }

    @Provides
    @Singleton
    fun provideCartUseCases(repository: CartRepository): CartUseCases {
        return CartUseCases(
            addToCart = AddToCartUseCase(repository),
            incrementQuantity = IncrementQuantityUseCase(repository),
            decrementQuantity = DecrementQuantityUseCase(repository),
            removeFromCart = RemoveFromCartUseCase(repository),
            clearCart = ClearCartUseCase(repository),
            getCartItems = GetCartItemsUseCase(repository)
        )
    }
}
