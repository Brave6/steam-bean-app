package com.coffeebean.di

import com.coffeebean.data.repository.FirebasePromoRepository
import com.coffeebean.domain.repository.PromoRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PromoRepositoryModule {

    @Provides
    @Singleton
    fun providePromoRepository(
        firestore: FirebaseFirestore
    ): PromoRepository {
        return FirebasePromoRepository(firestore)
    }
}
