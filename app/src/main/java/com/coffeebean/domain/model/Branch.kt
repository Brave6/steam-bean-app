package com.coffeebean.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Branch(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val phone: String = "",
    val operatingHours: String = "",
    val isOpen: Boolean = true,
    val imageUrl: String = ""
)