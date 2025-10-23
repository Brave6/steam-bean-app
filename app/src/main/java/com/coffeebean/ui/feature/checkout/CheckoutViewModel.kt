package com.coffeebean.ui.feature.checkout

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeebean.data.repository.BranchRepository
import com.coffeebean.data.repository.CartRepository
import com.coffeebean.domain.model.*
import com.coffeebean.ui.feature.menu.components.product.CartItem
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val isLoading: Boolean = true,
    val cartItems: List<CartItem> = emptyList(),
    val fulfillmentType: FulfillmentType = FulfillmentType.DELIVERY,
    val branches: List<Branch> = emptyList(),
    val selectedBranch: Branch? = null,
    val nearestBranch: Branch? = null,
    val userLocation: LatLng? = null,
    val deliveryAddress: DeliveryAddress? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val total: Double = 0.0,
    val error: String? = null,
    val isPlacingOrder: Boolean = false
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val branchRepository: BranchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState

    companion object {
        private const val DELIVERY_FEE = 50.0
        private const val FREE_DELIVERY_THRESHOLD = 500.0
    }

    init {
        loadCheckoutData()
    }

    private fun loadCheckoutData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Load cart items
                cartRepository.getCartItems().collect { items ->
                    val subtotal = items.sumOf { it.totalPrice }
                    val deliveryFee = calculateDeliveryFee(subtotal)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            cartItems = items,
                            subtotal = subtotal,
                            deliveryFee = deliveryFee,
                            total = subtotal + deliveryFee
                        )
                    }
                }

                // Load branches
                val branches = branchRepository.getBranches()
                _uiState.update { it.copy(branches = branches) }

            } catch (e: Exception) {
                Log.e("CheckoutVM", "Error loading checkout data: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load checkout data"
                    )
                }
            }
        }
    }

    fun setFulfillmentType(type: FulfillmentType) {
        _uiState.update {
            val deliveryFee = if (type == FulfillmentType.DELIVERY) {
                calculateDeliveryFee(it.subtotal)
            } else {
                0.0
            }
            it.copy(
                fulfillmentType = type,
                deliveryFee = deliveryFee,
                total = it.subtotal + deliveryFee
            )
        }
    }

    fun updateUserLocation(location: LatLng) {
        _uiState.update { it.copy(userLocation = location) }

        // Find nearest branch
        viewModelScope.launch {
            try {
                val nearest = branchRepository.getNearestBranch(
                    location.latitude,
                    location.longitude
                )
                _uiState.update {
                    it.copy(
                        nearestBranch = nearest,
                        selectedBranch = if (it.fulfillmentType == FulfillmentType.PICKUP) nearest else it.selectedBranch
                    )
                }
            } catch (e: Exception) {
                Log.e("CheckoutVM", "Error finding nearest branch: ${e.message}")
            }
        }
    }

    fun selectBranch(branch: Branch) {
        _uiState.update { it.copy(selectedBranch = branch) }
    }

    fun updateDeliveryAddress(address: DeliveryAddress) {
        _uiState.update { it.copy(deliveryAddress = address) }
    }

    fun setPaymentMethod(method: PaymentMethod) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun placeOrder(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isPlacingOrder = true) }

                val state = _uiState.value

                // Validation
                if (state.fulfillmentType == FulfillmentType.DELIVERY && state.deliveryAddress == null) {
                    _uiState.update {
                        it.copy(
                            isPlacingOrder = false,
                            error = "Please provide delivery address"
                        )
                    }
                    return@launch
                }

                if (state.fulfillmentType == FulfillmentType.PICKUP && state.selectedBranch == null) {
                    _uiState.update {
                        it.copy(
                            isPlacingOrder = false,
                            error = "Please select a branch"
                        )
                    }
                    return@launch
                }

                // Create order
                // TODO: Implement order creation in repository
                // val orderId = orderRepository.createOrder(order)

                // Clear cart
                cartRepository.clearCart()

                _uiState.update { it.copy(isPlacingOrder = false) }

                onSuccess("ORDER123") // Replace with actual order ID

            } catch (e: Exception) {
                Log.e("CheckoutVM", "Error placing order: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isPlacingOrder = false,
                        error = "Failed to place order: ${e.message}"
                    )
                }
            }
        }
    }

    private fun calculateDeliveryFee(subtotal: Double): Double {
        return if (subtotal >= FREE_DELIVERY_THRESHOLD) 0.0 else DELIVERY_FEE
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}