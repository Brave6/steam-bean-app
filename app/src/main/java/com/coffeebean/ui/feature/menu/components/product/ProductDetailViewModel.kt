package com.coffeebean.ui.feature.menu.components.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeebean.data.repository.CartRepository
import com.coffeebean.data.repository.FavoritesRepository
import com.coffeebean.domain.repository.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val cartRepository: CartRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading
            try {
                // Load product from repository
                val product = menuRepository.getProductById(productId)

                // Check if product is in favorites
                val isFavorite = favoritesRepository.isFavorite(productId)

                // Convert to ProductDetail
                val productDetail = ProductDetail(
                    id = product.id,
                    name = product.name,
                    description = product.description,
                    longDescription = product.longDescription,
                    price = product.price,
                    imageUrl = product.imageUrl,
                    category = product.category,
                    subcategory = product.subcategory,
                    available = product.available,
                    sizes = product.sizes.map {
                        ProductSize(
                            id = it.id,
                            name = it.name,
                            priceModifier = it.priceModifier
                        )
                    },
                    temperatures = product.temperatures.map {
                        ProductTemperature(
                            id = it.id,
                            name = it.name
                        )
                    },
                    nutritionInfo = product.nutritionInfo?.let {
                        NutritionInfo(
                            calories = it.calories,
                            caffeine = it.caffeine,
                            sugar = it.sugar
                        )
                    },
                    allergens = product.allergens
                )

                _uiState.value = ProductDetailUiState.Success(
                    product = productDetail,
                    quantity = 1,
                    selectedSize = productDetail.sizes.firstOrNull(),
                    selectedTemperature = productDetail.temperatures.firstOrNull(),
                    isFavorite = isFavorite
                )
            } catch (e: Exception) {
                _uiState.value = ProductDetailUiState.Error(
                    message = e.message ?: "Failed to load product details"
                )
            }
        }
    }

    fun updateQuantity(newQuantity: Int) {
        val currentState = _uiState.value
        if (currentState is ProductDetailUiState.Success) {
            _uiState.update {
                currentState.copy(quantity = newQuantity.coerceIn(1, 99))
            }
        }
    }

    fun selectSize(size: ProductSize) {
        val currentState = _uiState.value
        if (currentState is ProductDetailUiState.Success) {
            _uiState.update {
                currentState.copy(selectedSize = size)
            }
        }
    }

    fun selectTemperature(temperature: ProductTemperature) {
        val currentState = _uiState.value
        if (currentState is ProductDetailUiState.Success) {
            _uiState.update {
                currentState.copy(selectedTemperature = temperature)
            }
        }
    }

    fun toggleFavorite() {
        val currentState = _uiState.value
        if (currentState is ProductDetailUiState.Success) {
            viewModelScope.launch {
                try {
                    if (currentState.isFavorite) {
                        favoritesRepository.removeFromFavorites(currentState.product.id)
                    } else {
                        favoritesRepository.addToFavorites(currentState.product.id)
                    }
                    _uiState.update {
                        currentState.copy(isFavorite = !currentState.isFavorite)
                    }
                } catch (e: Exception) {
                    // Handle error silently or show a snackbar
                }
            }
        }
    }

    fun addToCart(onSuccess: () -> Unit = {}) {
        val currentState = _uiState.value
        if (currentState is ProductDetailUiState.Success) {
            viewModelScope.launch {
                try {
                    _uiState.update {
                        currentState.copy(isAddingToCart = true)
                    }

                    // Create cart item
                    val cartItem = createCartItem(currentState)

                    // Add to cart
                    cartRepository.addToCart(cartItem)

                    _uiState.update {
                        currentState.copy(isAddingToCart = false)
                    }

                    onSuccess()
                } catch (e: Exception) {
                    _uiState.update {
                        currentState.copy(isAddingToCart = false)
                    }
                    // Handle error - could emit an event to show snackbar
                }
            }
        }
    }

    private fun createCartItem(state: ProductDetailUiState.Success): CartItem {
        return CartItem(
            productId = state.product.id,
            productName = state.product.name,
            productImage = state.product.imageUrl,
            basePrice = state.product.price,
            quantity = state.quantity,
            selectedSize = state.selectedSize,
            selectedTemperature = state.selectedTemperature,
            totalPrice = calculateTotalPrice(state)
        )
    }

    private fun calculateTotalPrice(state: ProductDetailUiState.Success): Double {
        val basePrice = state.product.price
        val sizeModifier = state.selectedSize?.priceModifier ?: 0.0
        return (basePrice + sizeModifier) * state.quantity
    }
}

/**
 * Cart Item data class for repository
 */
data class CartItem(
    val id: String = "",
    val productId: String,
    val productName: String,
    val productImage: String,
    val basePrice: Double,
    val quantity: Int,
    val selectedSize: ProductSize?,
    val selectedTemperature: ProductTemperature?,
    val totalPrice: Double,
    val timestamp: Long = System.currentTimeMillis()
)