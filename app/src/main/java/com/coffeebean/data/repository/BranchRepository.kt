package com.coffeebean.data.repository

import com.coffeebean.domain.model.Branch
import kotlinx.coroutines.flow.Flow

interface BranchRepository {
    suspend fun getBranches(): List<Branch>
    suspend fun getNearestBranch(latitude: Double, longitude: Double): Branch?
    fun getBranchesFlow(): Flow<List<Branch>>
}