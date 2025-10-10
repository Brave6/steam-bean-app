package com.coffeebean.data.repository

import com.coffeebean.data.local.repository.ProductRepository
import com.coffeebean.domain.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseProductRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductRepository {

    override fun getProducts(): Flow<List<Product>> = callbackFlow {
        val collection = firestore.collection("products")

        // Listen for real-time updates
        val snapshotListener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Send error to the flow and close
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Map the documents to Product objects and send to the flow
                val products = snapshot.toObjects<Product>()
                trySend(products).isSuccess // Offer the new list to the flow
            }
        }

        // When the flow is cancelled, remove the listener
        awaitClose { snapshotListener.remove() }
    }
}
