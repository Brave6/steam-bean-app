package com.coffeebean.data.repository

import com.coffeebean.domain.model.Promo
import com.coffeebean.domain.repository.PromoRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebasePromoRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : PromoRepository {
    override suspend fun getPromos(): List<Promo> {
        return try {
            firestore.collection("promotions")
                .whereEqualTo("active", true)
                .get()
                .await()
                .toObjects(Promo::class.java)
        } catch (e: Exception) {
            // In a real app, you'd want to handle this error more gracefully
            // (e.g., log it, return a sealed result with an error state)
            emptyList()
        }
    }
}
