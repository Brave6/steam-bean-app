package com.coffeebean.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Promo(
    val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val deeplink: String = ""
)