package com.coffeebean.data.local.repository

import com.coffeebean.data.local.DummyProductData
import com.coffeebean.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
}

class DummyProductRepository : ProductRepository {
    override fun getProducts(): Flow<List<Product>> = flow {
        emit(DummyProductData.products)
    }
}
