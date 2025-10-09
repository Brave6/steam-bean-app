package com.coffeebean.ui.feature.menu

/**
 * Data class for menu items - prepare for Firebase integration
 */
data class MenuItem(
    val id: String = "",
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String = "",
    val category: MenuCategory,
    val subcategory: String? = null, // "Hot Coffee", "Iced Coffee", etc.
    val available: Boolean = true
)