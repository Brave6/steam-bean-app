package com.coffeebean.data.remote

import android.util.Log
import com.coffeebean.domain.model.Branch
import com.coffeebean.domain.model.Product
import com.coffeebean.domain.model.Promo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase client that handles all Firestore and Storage operations.
 * Centralizes Firebase-specific code and error handling.
 */
@Singleton
class FirebaseClient @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    companion object {
        private const val TAG = "FirebaseClient"
        private const val COLLECTION_PRODUCTS = "products"
        private const val COLLECTION_PROMOS = "promos"
    }

    // ========== PRODUCTS ==========

    /**
     * Fetches all available products ordered by category and name.
     * @throws FirebaseException if the operation fails
     */
    suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = firestore.collection(COLLECTION_PRODUCTS)
                .whereEqualTo("available", true)
                .orderBy("category", Query.Direction.ASCENDING)
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing product ${doc.id}: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching products: ${e.message}", e)
            throw FirebaseException("Failed to fetch products", e)
        }
    }

    /**
     * Fetches a single product by ID.
     * @param productId The ID of the product to fetch
     * @throws FirebaseException if the product is not found or operation fails
     */
    suspend fun getProductById(productId: String): Product {
        return try {
            val document = firestore.collection(COLLECTION_PRODUCTS)
                .document(productId)
                .get()
                .await()

            if (!document.exists()) {
                throw FirebaseException("Product not found: $productId")
            }

            document.toObject(Product::class.java)?.copy(id = document.id)
                ?: throw FirebaseException("Failed to parse product data")
        } catch (e: FirebaseException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching product $productId: ${e.message}", e)
            throw FirebaseException("Failed to fetch product", e)
        }
    }

    /**
     * Fetches products by category.
     * @param category The category to filter by (case-insensitive)
     */
    suspend fun getProductsByCategory(category: String): List<Product> {
        return try {
            val snapshot = firestore.collection(COLLECTION_PRODUCTS)
                .whereEqualTo("category", category.lowercase())
                .whereEqualTo("available", true)
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing product ${doc.id}: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching products by category $category: ${e.message}", e)
            throw FirebaseException("Failed to fetch products by category", e)
        }
    }

    /**
     * Searches products by name (case-insensitive).
     * Note: For better search, consider using Algolia or similar service.
     */
    suspend fun searchProducts(query: String): List<Product> {
        return try {
            val snapshot = firestore.collection(COLLECTION_PRODUCTS)
                .whereEqualTo("available", true)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing product ${doc.id}: ${e.message}")
                    null
                }
            }.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        product.description.contains(query, ignoreCase = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching products: ${e.message}", e)
            throw FirebaseException("Failed to search products", e)
        }
    }

    // ========== PROMOS ==========

    /**
     * Fetches all active promos ordered by priority.
     * @throws FirebaseException if the operation fails
     */
    suspend fun getPromos(): List<Promo> {
        return try {
            val snapshot = firestore.collection(COLLECTION_PROMOS)
                .whereEqualTo("active", true)
                .orderBy("priority", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Promo::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing promo ${doc.id}: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching promos: ${e.message}", e)
            throw FirebaseException("Failed to fetch promos", e)
        }
    }

    /**
     * Fetches a single promo by ID.
     */
    suspend fun getPromoById(promoId: String): Promo {
        return try {
            val document = firestore.collection(COLLECTION_PROMOS)
                .document(promoId)
                .get()
                .await()

            if (!document.exists()) {
                throw FirebaseException("Promo not found: $promoId")
            }

            document.toObject(Promo::class.java)?.copy(id = document.id)
                ?: throw FirebaseException("Failed to parse promo data")
        } catch (e: FirebaseException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching promo $promoId: ${e.message}", e)
            throw FirebaseException("Failed to fetch promo", e)
        }
    }

    // ========== FAVORITES ==========

    /**
     * Gets user's favorite product IDs.
     * @param userId The user's ID
     */
    suspend fun getFavorites(userId: String): List<String> {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            @Suppress("UNCHECKED_CAST")
            (document.get("favorites") as? List<String>) ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching favorites: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Adds a product to user's favorites.
     */
    suspend fun addToFavorites(userId: String, productId: String) {
        try {
            val userRef = firestore.collection("users").document(userId)
            val document = userRef.get().await()

            if (document.exists()) {
                val currentFavorites = document.get("favorites") as? List<*> ?: emptyList<String>()
                if (!currentFavorites.contains(productId)) {
                    userRef.update("favorites", currentFavorites + productId).await()
                }
            } else {
                userRef.set(mapOf("favorites" to listOf(productId))).await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding to favorites: ${e.message}", e)
            throw FirebaseException("Failed to add to favorites", e)
        }
    }

    /**
     * Removes a product from user's favorites.
     */
    suspend fun removeFromFavorites(userId: String, productId: String) {
        try {
            val userRef = firestore.collection("users").document(userId)
            val document = userRef.get().await()

            if (document.exists()) {
                val currentFavorites = document.get("favorites") as? List<*> ?: emptyList<String>()
                userRef.update("favorites", currentFavorites.filter { it != productId }).await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing from favorites: ${e.message}", e)
            throw FirebaseException("Failed to remove from favorites", e)
        }
    }

    // ========== CART ==========

    /**
     * Gets user's cart items.
     */
    suspend fun getCartItems(userId: String): List<CartItemData> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("cart")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(CartItemData::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing cart item ${doc.id}: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching cart items: ${e.message}", e)
            throw FirebaseException("Failed to fetch cart items", e)
        }
    }

    /**
     * Adds an item to user's cart.
     */
    suspend fun addToCart(userId: String, item: CartItemData) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("cart")
                .add(item)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error adding to cart: ${e.message}", e)
            throw FirebaseException("Failed to add to cart", e)
        }
    }

    /**
     * Updates cart item quantity.
     */
    suspend fun updateCartItem(userId: String, itemId: String, quantity: Int, totalPrice: Double) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("cart")
                .document(itemId)
                .update(
                    mapOf(
                        "quantity" to quantity,
                        "totalPrice" to totalPrice,
                        "timestamp" to System.currentTimeMillis()
                    )
                )
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating cart item: ${e.message}", e)
            throw FirebaseException("Failed to update cart item", e)
        }
    }

    /**
     * Removes an item from cart.
     */
    suspend fun removeFromCart(userId: String, itemId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("cart")
                .document(itemId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error removing from cart: ${e.message}", e)
            throw FirebaseException("Failed to remove from cart", e)
        }
    }

    /**
     * Clears all items from user's cart.
     */
    suspend fun clearCart(userId: String) {
        try {
            val cartItems = firestore.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .await()

            firestore.runBatch { batch ->
                cartItems.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
            }.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing cart: ${e.message}", e)
            throw FirebaseException("Failed to clear cart", e)
        }
    }

    suspend fun getBranches(): List<Branch> {
        return try {
            val snapshot = firestore.collection("branches")
                .whereEqualTo("isOpen", true)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Branch::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing branch ${doc.id}: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching branches: ${e.message}", e)
            throw FirebaseException("Failed to fetch branches", e)
        }
    }


    // ========== STORAGE ==========

    /**
     * Uploads an image to Firebase Storage.
     * @param path The storage path (e.g., "products/image.jpg")
     * @param data The image data as ByteArray
     * @return The download URL of the uploaded image
     */
    suspend fun uploadImage(path: String, data: ByteArray): String {
        return try {
            val ref = storage.reference.child(path)
            ref.putBytes(data).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading image: ${e.message}", e)
            throw FirebaseException("Failed to upload image", e)
        }
    }

    /**
     * Deletes an image from Firebase Storage.
     */
    suspend fun deleteImage(url: String) {
        try {
            storage.getReferenceFromUrl(url).delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting image: ${e.message}", e)
            throw FirebaseException("Failed to delete image", e)
        }
    }
}

// ========== DATA CLASSES ==========

/**
 * Cart item data class for Firestore.
 */
data class CartItemData(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val basePrice: Double = 0.0,
    val quantity: Int = 0,
    val selectedSize: ProductSizeData? = null,
    val selectedTemperature: ProductTemperatureData? = null,
    val totalPrice: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

data class ProductSizeData(
    val id: String = "",
    val name: String = "",
    val priceModifier: Double = 0.0
)

data class ProductTemperatureData(
    val id: String = "",
    val name: String = ""
)

/**
 * Custom exception for Firebase operations.
 */
class FirebaseException(message: String, cause: Throwable? = null) : Exception(message, cause)