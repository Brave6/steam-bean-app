package com.coffeebean.data.repository

import com.coffeebean.data.local.repository.AuthRepository
import com.coffeebean.ui.feature.signup.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun registerUser(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override fun googleSignIn(idToken: String): Flow<Resource<AuthResult>> {
        // This would require a more complex setup with Google Sign-In
        // For now, we'll return an error
        return flow { emit(Resource.Error("Google Sign-In is not implemented")) }
    }

    override fun getCurrentUser(): Any? {
        return firebaseAuth.currentUser
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}
