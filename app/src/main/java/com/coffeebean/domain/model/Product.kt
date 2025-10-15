package com.coffeebean.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val longDescription: String? = null,        // NEW
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "",
    val subcategory: String? = null,            // NEW
    val available: Boolean = true,              // NEW
    val sizes: List<ProductSizeData> = emptyList(),        // NEW
    val temperatures: List<ProductTemperatureData> = emptyList(),  // NEW
    val nutritionInfo: ProductNutritionInfo? = null,       // NEW
    val allergens: List<String> = emptyList(),  // NEW
    val createdAt: Long = 0L,                   // NEW
    val updatedAt: Long = 0L                    // NEW
)

@Serializable
data class ProductNutritionInfo(
    val calories: Int = 0,
    val caffeine: Int? = null,
    val sugar: Int? = null
)

@Serializable
data class ProductSizeData(
    val id: String = "",
    val name: String = "",
    val priceModifier: Double = 0.0
)
@Serializable
data class ProductTemperatureData(
    val id: String = "",
    val name: String = ""
)


