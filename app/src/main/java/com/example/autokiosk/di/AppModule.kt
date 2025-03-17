package com.example.autokiosk.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.autokiosk.data.local.CartDao
import com.example.autokiosk.data.local.AppDatabase
import com.example.autokiosk.data.local.CardDao
import com.example.autokiosk.data.local.MIGRATION_2_3
import com.example.autokiosk.data.repository.CardRepositoryImpl
import com.example.autokiosk.data.repository.CartRepositoryImpl
import com.example.autokiosk.data.repository.ProductRepositoryImpl
import com.example.autokiosk.domain.repository.CardRepository
import com.example.autokiosk.domain.repository.CartRepository
import com.example.autokiosk.domain.repository.ProductRepository
import com.example.autokiosk.domain.usecase.AddCardUseCase
import com.example.autokiosk.domain.usecase.AddToCartUseCase
import com.example.autokiosk.domain.usecase.CardUseCases
import com.example.autokiosk.domain.usecase.CartUseCases
import com.example.autokiosk.domain.usecase.ClearCartUseCase
import com.example.autokiosk.domain.usecase.DecrementQuantityUseCase
import com.example.autokiosk.domain.usecase.GetAllCardsUseCase
import com.example.autokiosk.domain.usecase.GetCartItemQuantityUseCase
import com.example.autokiosk.domain.usecase.GetCartItemsUseCase
import com.example.autokiosk.domain.usecase.IncrementQuantityUseCase
import com.example.autokiosk.domain.usecase.RemoveFromCartUseCase
import com.example.autokiosk.domain.usecase.GetCategoriesUseCase
import com.example.autokiosk.domain.usecase.GetProductByIdUseCase
import com.example.autokiosk.domain.usecase.GetProductsUseCase
import com.example.autokiosk.domain.usecase.ProductUseCases
import com.example.autokiosk.domain.usecase.RemoveCardUseCase
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

    @Binds
    @Singleton
    abstract fun bindCardRepository(
        cardRepositoryImpl: CardRepositoryImpl
    ): CardRepository
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
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .addMigrations(MIGRATION_2_3)
             .build()
    }

    @Provides
    @Singleton
    fun provideCartDao(appDatabase: AppDatabase): CartDao {
        return appDatabase.cartDao()
    }

    @Provides
    @Singleton
    fun provideCardDao(appDatabase: AppDatabase): CardDao {
        return appDatabase.cardDao()
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
            getCartItems = GetCartItemsUseCase(repository),
            getCartItemQuantity = GetCartItemQuantityUseCase(repository)
        )
    }

    @Provides
    fun provideCardUseCases(repository: CardRepository): CardUseCases {
        return CardUseCases(
            getAllCards = GetAllCardsUseCase(repository),
            addCard = AddCardUseCase(repository),
            removeCard = RemoveCardUseCase(repository)
        )
    }
}
