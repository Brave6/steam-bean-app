package com.coffeebean.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeebean.data.local.repository.ProductRepository
import com.coffeebean.domain.model.Product
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    // Existing products state
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    // New: Logout dialog state
    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog

    init {
        viewModelScope.launch {
            productRepository.getProducts().collectLatest { _products.value = it }
        }
    }

    // Called when user presses back button
    fun onBackPressed() {
        _showLogoutDialog.value = true
    }

    // Confirm logout
    fun confirmLogout() {
        firebaseAuth.signOut()
        _showLogoutDialog.value = false
    }

    // Dismiss dialog without logging out
    fun dismissLogoutDialog() {
        _showLogoutDialog.value = false
    }
}
