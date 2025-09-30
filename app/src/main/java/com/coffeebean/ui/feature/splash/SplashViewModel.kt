package com.coffeebean.ui.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeebean.data.local.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    dataStore: DataStoreManager
) : ViewModel() {
    val onboardingCompleted = dataStore.onboardingCompleted
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), false)
}