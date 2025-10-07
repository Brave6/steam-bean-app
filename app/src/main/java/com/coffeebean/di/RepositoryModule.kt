package com.coffeebean.di

import com.coffeebean.data.local.repository.DummyProductRepository
import com.coffeebean.data.local.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        dummyProductRepository: DummyProductRepository
    ): ProductRepository
}
