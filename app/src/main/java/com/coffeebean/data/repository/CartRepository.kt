package com.coffeebean.data.repository

import com.coffeebean.ui.feature.menu.components.product.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun addToCart(item: CartItem)
    suspend fun updateCartItem(itemId: String, quantity: Int)
    suspend fun removeFromCart(itemId: String)
    suspend fun clearCart()
    fun getCartItems(): Flow<List<CartItem>>
    fun getCartItemCount(): Flow<Int>
    fun getCartTotal(): Flow<Double>
}