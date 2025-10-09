package com.coffeebean.domain.repository

import com.coffeebean.data.remote.FirebaseClient
import com.coffeebean.domain.model.Product
import com.coffeebean.domain.model.Promo
import javax.inject.Inject

class MenuRepository @Inject constructor(
    private val firebaseClient: FirebaseClient
) {

    suspend fun getProducts(): List<Product> {
        return firebaseClient.getProducts()
    }

    suspend fun getPromos(): List<Promo> {
        return firebaseClient.getPromos()
    }
}