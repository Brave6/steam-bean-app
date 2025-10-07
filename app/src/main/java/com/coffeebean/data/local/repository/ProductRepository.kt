package com.coffeebean.data.local.repository

import com.coffeebean.data.local.DummyProductData
import com.coffeebean.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
}

class DummyProductRepository @Inject constructor(
    // Any dependencies also provided by Hilt can go here
) : ProductRepository {    override fun getProducts(): Flow<List<Product>> = flow {
        emit(DummyProductData.products)
    }
}
