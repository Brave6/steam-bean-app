// ui/feature/signup/SignupUiState.kt
package com.coffeebean.ui.feature.signup

data class SignupUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
