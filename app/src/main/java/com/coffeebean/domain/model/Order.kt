package com.coffeebean.domain.model

import com.coffeebean.ui.feature.menu.components.product.CartItem
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val fulfillmentType: FulfillmentType = FulfillmentType.DELIVERY,
    val deliveryAddress: DeliveryAddress? = null,
    val pickupBranch: Branch? = null,
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val total: Double = 0.0,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val estimatedDeliveryTime: Long? = null
)

@Serializable
data class DeliveryAddress(
    val fullAddress: String = "",
    val landmark: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val instructions: String = ""
)

enum class FulfillmentType {
    DELIVERY, PICKUP
}

enum class PaymentMethod {
    CASH, GCASH, CARD
}

enum class OrderStatus {
    PENDING, CONFIRMED, PREPARING, READY, OUT_FOR_DELIVERY, COMPLETED, CANCELLED
}