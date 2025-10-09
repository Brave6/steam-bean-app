package com.coffeebean.data.remote

import com.coffeebean.domain.model.Product
import com.coffeebean.domain.model.Promo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseClient @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    // We'll add methods to fetch data here

    suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = firestore.collection("products").get().await()
            snapshot.toObjects<Product>()
        } catch (e: Exception) {
            // You can handle the exception more gracefully, e.g., by logging it
            emptyList()
        }
    }

    suspend fun getPromos(): List<Promo> {
        return try {
            val snapshot = firestore.collection("promos").get().await()
            snapshot.toObjects<Promo>()
        } catch (e: Exception) {
            // You can handle the exception more gracefully, e.g., by logging it
            emptyList()
        }
    }
}