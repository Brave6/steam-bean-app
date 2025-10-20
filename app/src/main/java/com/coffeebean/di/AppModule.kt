package com.coffeebean.di

import com.coffeebean.data.local.repository.AuthRepository
import com.coffeebean.data.local.repository.ProductRepository
import com.coffeebean.data.remote.FirebaseClient
import com.coffeebean.data.FirebaseAuthRepository
import com.coffeebean.data.remote.CartRepositoryImpl
import com.coffeebean.data.remote.FavoritesRepositoryImpl
import com.coffeebean.data.remote.MenuRepositoryImpl
import com.coffeebean.data.repository.CartRepository
import com.coffeebean.data.repository.FavoritesRepository
import com.coffeebean.data.repository.FirebaseProductRepository
import com.coffeebean.data.repository.FirebasePromoRepository
import com.coffeebean.domain.repository.MenuRepository
import com.coffeebean.domain.repository.PromoRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseClient(firestore: FirebaseFirestore, storage: FirebaseStorage): FirebaseClient {
        return FirebaseClient(firestore, storage)
    }

    @Provides
    @Singleton
    fun provideProductRepository(impl: FirebaseProductRepository): ProductRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideMenuRepository(firebaseClient: FirebaseClient): MenuRepository {
        return MenuRepositoryImpl(firebaseClient)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(impl: FirebaseAuthRepository): AuthRepository {
        return impl
    }

    @Provides
    @Singleton
    fun providePromoRepository(impl: FirebasePromoRepository): PromoRepository {
        return impl
    }
    // ========== NEW: Cart Repository ==========
    @Provides
    @Singleton
    fun provideCartRepository(
        firebaseClient: FirebaseClient,
        auth: FirebaseAuth,
        firestore: FirebaseFirestore  // 🔥 Add this
    ): CartRepository {
        return CartRepositoryImpl(firebaseClient, auth, firestore)
    }

    // ========== NEW: Favorites Repository ==========
    @Provides
    @Singleton
    fun provideFavoritesRepository(
        firebaseClient: FirebaseClient,
        auth: FirebaseAuth
    ): FavoritesRepository {
        return FavoritesRepositoryImpl(firebaseClient, auth)
    }
}

