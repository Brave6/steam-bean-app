package com.coffeebean.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Promo(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val targetScreen: String? = null,

    // Fields for Firebase Analytics promotion tracking
    val promotionId: String = "",
    val promotionName: String = "",
    val creativeName: String = "",
    val creativeSlot: String = "",
    val locationId: String = "HOME_PROMO_CAROUSEL", // A default location ID
    val active: Boolean = true
)