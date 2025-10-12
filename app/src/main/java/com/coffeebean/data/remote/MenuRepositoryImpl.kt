package com.coffeebean.data.remote

import com.coffeebean.data.remote.FirebaseClient
import com.coffeebean.domain.model.Product
import com.coffeebean.domain.model.Promo
import com.coffeebean.domain.repository.MenuRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuRepositoryImpl @Inject constructor(
    private val firebaseClient: FirebaseClient
) : MenuRepository {

    override suspend fun getProducts(): List<Product> {
        return firebaseClient.getProducts()
    }

    override suspend fun getProductById(productId: String): Product {
        return firebaseClient.getProductById(productId)
    }

    override suspend fun getProductsByCategory(category: String): List<Product> {
        return firebaseClient.getProductsByCategory(category)
    }

    override suspend fun getPromos(): List<Promo> {
        return firebaseClient.getPromos()
    }

    suspend fun searchProducts(query: String): List<Product> {
        return firebaseClient.searchProducts(query)
    }
}