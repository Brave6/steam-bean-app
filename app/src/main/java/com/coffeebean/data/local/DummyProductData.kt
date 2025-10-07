package com.coffeebean.data.local

import com.coffeebean.domain.model.Product
import com.coffeebean.R

object DummyProductData {
    val products = listOf(
        Product(1, "Cappuccino", "with Chocolate", "₱125.00", R.drawable.brew),
        Product(2, "Cold Brew", "with Milk Foam", "₱145.00", R.drawable.cold_brew),
        Product(3, "Espresso", "strong & bold", "₱165.00", R.drawable.vanilla),
        Product(4, "Mocha", "with Cream", "₱4.80", R.drawable.brew),
        Product(5, "Americano", "classic black", "₱3.50", R.drawable.brew)
    )
}
