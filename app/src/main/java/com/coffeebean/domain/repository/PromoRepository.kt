package com.coffeebean.domain.repository

import com.coffeebean.domain.model.Promo

interface PromoRepository {
    /**
     * Fetches all active promotional items.
     *
     * @return A list of [Promo] objects.
     */
    suspend fun getPromos(): List<Promo>
}
