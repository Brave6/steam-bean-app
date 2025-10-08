package com.coffeebean.data.local.repository

import com.coffeebean.ui.feature.signup.Resource
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun registerUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun loginUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun getCurrentUser(): Any?
    fun logout()
}
