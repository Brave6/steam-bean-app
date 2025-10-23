package com.coffeebean.data.remote

import android.location.Location
import com.coffeebean.data.remote.FirebaseClient
import com.coffeebean.data.repository.BranchRepository
import com.coffeebean.domain.model.Branch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BranchRepositoryImpl @Inject constructor(
    private val firebaseClient: FirebaseClient
) : BranchRepository {

    override suspend fun getBranches(): List<Branch> {
        return firebaseClient.getBranches()
    }

    override suspend fun getNearestBranch(latitude: Double, longitude: Double): Branch? {
        val branches = getBranches()
        if (branches.isEmpty()) return null

        return branches.minByOrNull { branch ->
            calculateDistance(latitude, longitude, branch.latitude, branch.longitude)
        }
    }

    override fun getBranchesFlow(): Flow<List<Branch>> = flow {
        emit(getBranches())
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] // Distance in meters
    }
}