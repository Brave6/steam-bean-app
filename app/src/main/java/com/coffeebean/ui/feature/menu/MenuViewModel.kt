package com.coffeebean.ui.feature.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeebean.domain.repository.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val uiState: StateFlow<MenuUiState> = _uiState

    init {
        getMenuItems()
    }

    private fun getMenuItems() {
        viewModelScope.launch {
            try {
                val products = menuRepository.getProducts()
                val menuItems = products.map {
                    MenuItem(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        price = it.price,
                        imageUrl = it.imageUrl,
                        category = MenuCategory.valueOf(it.category.uppercase()),
                    )
                }.groupBy { it.category }

                _uiState.value = MenuUiState.Success(menuItems)
            } catch (e: Exception) {
                _uiState.value = MenuUiState.Error("Failed to load menu items.")
            }
        }
    }
}