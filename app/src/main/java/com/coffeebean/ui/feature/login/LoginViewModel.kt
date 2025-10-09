package com.coffeebean.ui.feature.login

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeebean.data.local.repository.AuthRepository
import com.coffeebean.ui.feature.signup.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            authRepository.loginUser(email, password).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                    is Resource.Success -> _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                    is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun googleSignIn(idToken: String) {
        Log.d(TAG, "googleSignIn called with token")
        viewModelScope.launch {
            authRepository.googleSignIn(idToken).collect { result ->
                Log.d(TAG, "Google Sign-In result: $result")
                when (result) {
                    is Resource.Loading -> {
                        Log.d(TAG, "Google Sign-In loading")
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                    }
                    is Resource.Success -> {
                        Log.d(TAG, "Google Sign-In success")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            googleSignInSuccess = true,
                            errorMessage = null
                        )
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Google Sign-In error: ${result.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun setError(message: String) {
        Log.e(TAG, "Setting error: $message")
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = message
        )
    }

    fun onNavigationHandled() {
        Log.d(TAG, "onNavigationHandled called")
        _uiState.value = _uiState.value.copy(
            isSuccess = false,
            googleSignInSuccess = false,
            errorMessage = null
        )
    }
}
// State class for UI
data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val googleSignInSuccess: Boolean = false
)
