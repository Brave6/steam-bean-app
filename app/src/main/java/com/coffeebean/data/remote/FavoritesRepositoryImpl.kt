package com.coffeebean.data.remote

import com.coffeebean.data.repository.FavoritesRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val firebaseClient: FirebaseClient,
    private val auth: FirebaseAuth
) : FavoritesRepository {

    private fun getUserId(): String {
        return auth.currentUser?.uid ?: throw Exception("User not authenticated")
    }

    override suspend fun addToFavorites(productId: String) {
        val userId = getUserId()
        firebaseClient.addToFavorites(userId, productId)
    }

    override suspend fun removeFromFavorites(productId: String) {
        val userId = getUserId()
        firebaseClient.removeFromFavorites(userId, productId)
    }

    @Suppress("CONFLICTING_CANDIDATE")
    override suspend fun isFavorite(productId: String): Boolean {
        val userId = getUserId()
        val favorites = firebaseClient.getFavorites(userId)
        return favorites.contains(productId)
    }

    override fun getFavorites(): Flow<List<String>> = flow {
        val userId = getUserId()
        val favorites = firebaseClient.getFavorites(userId)
        emit(favorites)
    }
}