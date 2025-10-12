package com.coffeebean.data.repository

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addToFavorites(productId: String)
    suspend fun removeFromFavorites(productId: String)
    suspend fun isFavorite(productId: String): Boolean
    fun getFavorites(): Flow<List<String>>
}