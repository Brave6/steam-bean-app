package com.coffeebean.data

import com.coffeebean.data.local.repository.AuthRepository
import com.coffeebean.ui.feature.signup.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun registerUser(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Registration failed"))
        }
    }

    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Login failed"))
        }
    }

    override fun googleSignIn(idToken: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Google Sign-In failed"))
        }
    }

    override fun getCurrentUser() = firebaseAuth.currentUser

    override fun logout() = firebaseAuth.signOut()
}
