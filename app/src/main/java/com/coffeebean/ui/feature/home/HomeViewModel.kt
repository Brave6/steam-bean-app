package com.coffeebean.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeebean.data.local.repository.ProductRepository
import com.coffeebean.domain.model.Product
import com.coffeebean.domain.model.Promo
import com.coffeebean.domain.repository.PromoRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

// UI state for the promo carousel
sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val promos: List<Promo>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val promoRepository: PromoRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    // State for the promo carousel
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Existing state for products
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Existing state for logout dialog
    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog.asStateFlow()

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    init {
        fetchProducts()
        fetchPromos()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            productRepository.getProducts().collectLatest { productList ->
                _products.value = productList
            }
        }
    }

    private fun fetchPromos() {
        viewModelScope.launch {
            val promoList = promoRepository.getPromos()
            _uiState.value = HomeUiState.Success(promoList)
        }
    }

    fun onLogoutClicked() {
        _showLogoutDialog.value = true
    }

    fun onBackPressed() {
        _showLogoutDialog.value = true
    }

    fun confirmLogout() {
        viewModelScope.launch {
            _isLoggingOut.value = true
            _showLogoutDialog.value = false
            delay(50) // Tiny delay to let dialog animation finish
            firebaseAuth.signOut()
        }
    }

    fun dismissLogoutDialog() {
        _showLogoutDialog.value = false
    }
}