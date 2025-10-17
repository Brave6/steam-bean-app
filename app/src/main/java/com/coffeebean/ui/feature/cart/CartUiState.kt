package com.coffeebean.ui.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeebean.data.repository.CartRepository
import com.coffeebean.ui.feature.menu.components.product.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CartUiState {
    data object Loading : CartUiState()
    data class Success(
        val items: List<CartItem>,
        val total: Double,
        val itemCount: Int
    ) : CartUiState()
    data class Empty : CartUiState()
    data class Error(val message: String) : CartUiState()
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState: StateFlow<CartUiState> = _uiState

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _uiState.value = CartUiState.Loading
            
            try {
                // Collect cart items as Flow
                cartRepository.getCartItems()
                    .catch { e ->
                        _uiState.value = CartUiState.Error(
                            e.message ?: "Failed to load cart"
                        )
                    }
                    .collect { items ->
                        if (items.isEmpty()) {
                            _uiState.value = CartUiState.Empty
                        } else {
                            val total = items.sumOf { it.totalPrice }
                            val itemCount = items.sumOf { it.quantity }
                            
                            _uiState.value = CartUiState.Success(
                                items = items,
                                total = total,
                                itemCount = itemCount
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = CartUiState.Error(
                    e.message ?: "Failed to load cart"
                )
            }
        }
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        viewModelScope.launch {
            try {
                if (newQuantity <= 0) {
                    removeItem(itemId)
                } else {
                    cartRepository.updateCartItem(itemId, newQuantity)
                    // Cart will auto-refresh via Flow
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            try {
                cartRepository.removeFromCart(itemId)
                // Cart will auto-refresh via Flow
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                cartRepository.clearCart()
                // Cart will auto-refresh via Flow
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}