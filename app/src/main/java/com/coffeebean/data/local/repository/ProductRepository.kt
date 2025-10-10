package com.coffeebean.data.local.repository

import com.coffeebean.domain.model.Product
import kotlinx.coroutines.flow.Flow


interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
}


