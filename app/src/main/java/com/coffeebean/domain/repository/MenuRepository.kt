package com.coffeebean.domain.repository

import com.coffeebean.domain.model.Product
import com.coffeebean.domain.model.Promo

interface MenuRepository {
    suspend fun getProducts(): List<Product>
    suspend fun getProductById(productId: String): Product
    suspend fun getProductsByCategory(category: String): List<Product>
    suspend fun getPromos(): List<Promo>
}