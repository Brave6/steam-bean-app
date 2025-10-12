package com.coffeebean.data.remote

import com.coffeebean.data.repository.CartRepository
import com.coffeebean.ui.feature.menu.components.product.ProductSize
import com.coffeebean.ui.feature.menu.components.product.ProductTemperature
import com.coffeebean.ui.feature.menu.components.product.CartItem

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class CartRepositoryImpl @Inject constructor(
    private val firebaseClient: FirebaseClient,
    private val auth: FirebaseAuth
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
        val items = firebaseClient.getCartItems(userId)
        val item = items.find { it.id == itemId }
            ?: throw Exception("Cart item not found")

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

    override fun getCartItems(): Flow<List<CartItem>> = flow {
        val userId = getUserId()
        val items = firebaseClient.getCartItems(userId)
        
        val cartItems = items.map { data ->
            CartItem(
                id = data.id,
                productId = data.productId,
                productName = data.productName,
                productImage = data.productImage,
                basePrice = data.basePrice,
                quantity = data.quantity,
                selectedSize = data.selectedSize?.let {
                    ProductSize(it.id, it.name, it.priceModifier)
                },
                selectedTemperature = data.selectedTemperature?.let {
                    ProductTemperature(it.id, it.name)
                },
                totalPrice = data.totalPrice,
                timestamp = data.timestamp
            )
        }
        
        emit(cartItems)
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