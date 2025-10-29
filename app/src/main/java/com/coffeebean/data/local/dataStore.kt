package com.coffeebean.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

    companion object {
        private val ONBOARDING_KEY = booleanPreferencesKey("onboarding_completed")
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[ONBOARDING_KEY] ?: false }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_KEY] = completed
        }
    }
}