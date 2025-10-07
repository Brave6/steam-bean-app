package com.coffeebean.data.local.repository

import com.coffeebean.data.local.repository.ProductRepository
import com.coffeebean.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeProductRepository(private val products: List<Product>) : ProductRepository {
    override fun getProducts(): Flow<List<Product>> = flowOf(products)
}
