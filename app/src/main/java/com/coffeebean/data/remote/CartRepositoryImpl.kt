package com.coffeebean.data.remote

import android.util.Log
import com.coffeebean.data.repository.CartRepository
import com.coffeebean.ui.feature.menu.components.product.ProductSize
import com.coffeebean.ui.feature.menu.components.product.ProductTemperature
import com.coffeebean.ui.feature.menu.components.product.CartItem

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class CartRepositoryImpl @Inject constructor(
    private val firebaseClient: FirebaseClient,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore

) : CartRepository {

    private fun getUserId(): String {
        return auth.currentUser?.uid ?: throw Exception("User not authenticated")
    }

    override suspend fun addToCart(item: CartItem) {
        val userId = getUserId()
        
        // Check for existing item with same configuration
        val existingItems = firebaseClient.getCartItems(userId)
        val existingItem = existingItems.find {
            it.productId == item.productId &&
            it.selectedSize?.id == item.selectedSize?.id &&
            it.selectedTemperature?.id == item.selectedTemperature?.id
        }

        if (existingItem != null) {
            // Update existing item quantity
            val newQuantity = existingItem.quantity + item.quantity
            val newTotalPrice = (item.basePrice + (item.selectedSize?.priceModifier ?: 0.0)) * newQuantity
            firebaseClient.updateCartItem(userId, existingItem.id, newQuantity, newTotalPrice)
        } else {
            // Add new item
            val cartItemData = CartItemData(
                productId = item.productId,
                productName = item.productName,
                productImage = item.productImage,
                basePrice = item.basePrice,
                quantity = item.quantity,
                selectedSize = item.selectedSize?.let {
                    ProductSizeData(it.id, it.name, it.priceModifier)
                },
                selectedTemperature = item.selectedTemperature?.let {
                    ProductTemperatureData(it.id, it.name)
                },
                totalPrice = item.totalPrice,
                timestamp = System.currentTimeMillis()
            )
            firebaseClient.addToCart(userId, cartItemData)
        }
    }

    override suspend fun updateCartItem(itemId: String, quantity: Int) {
        val userId = getUserId()

        // 🔥 FIX: Get fresh data from Firestore
        val document = firestore.collection("users")
            .document(userId)
            .collection("cart")
            .document(itemId)
            .get()
            .await()

        if (!document.exists()) {
            Log.e("CartRepository", "❌ Cart item not found: $itemId")
            throw Exception("Cart item not found")
        }

        val item = document.toObject(CartItemData::class.java)
            ?: throw Exception("Failed to parse cart item")

        val newTotalPrice = (item.basePrice + (item.selectedSize?.priceModifier ?: 0.0)) * quantity
        firebaseClient.updateCartItem(userId, itemId, quantity, newTotalPrice)
    }

    override suspend fun removeFromCart(itemId: String) {
        val userId = getUserId()
        firebaseClient.removeFromCart(userId, itemId)
    }

    override suspend fun clearCart() {
        val userId = getUserId()
        firebaseClient.clearCart(userId)
    }

    override fun getCartItems(): Flow<List<CartItem>> = callbackFlow {
        val userId = getUserId()

        Log.d("CartRepository", "🔄 Setting up real-time cart listener for user: $userId")

        // 🔥 Add Firestore snapshot listener for real-time updates
        val listener = firestore.collection("users")
            .document(userId)
            .collection("cart")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CartRepository", "❌ Error listening to cart: ${error.message}", error)
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        try {
                            val data = doc.toObject(CartItemData::class.java)?.copy(id = doc.id)
                            data?.let {
                                CartItem(
                                    id = it.id,
                                    productId = it.productId,
                                    productName = it.productName,
                                    productImage = it.productImage,
                                    basePrice = it.basePrice,
                                    quantity = it.quantity,
                                    selectedSize = it.selectedSize?.let { size ->
                                        ProductSize(size.id, size.name, size.priceModifier)
                                    },
                                    selectedTemperature = it.selectedTemperature?.let { temp ->
                                        ProductTemperature(temp.id, temp.name)
                                    },
                                    totalPrice = it.totalPrice,
                                    timestamp = it.timestamp
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("CartRepository", "Error parsing cart item: ${e.message}")
                            null
                        }
                    }

                    Log.d("CartRepository", "✅ Cart updated: ${items.size} items")
                    trySend(items).isSuccess
                }
            }

        // 🔥 Remove listener when Flow is cancelled
        awaitClose {
            Log.d("CartRepository", "🔴 Removing cart listener")
            listener.remove()
        }
    }

    override fun getCartItemCount(): Flow<Int> = flow {
        val userId = getUserId()
        val items = firebaseClient.getCartItems(userId)
        emit(items.sumOf { it.quantity })
    }

    override fun getCartTotal(): Flow<Double> = flow {
        val userId = getUserId()
        val items = firebaseClient.getCartItems(userId)
        emit(items.sumOf { it.totalPrice })
    }
}