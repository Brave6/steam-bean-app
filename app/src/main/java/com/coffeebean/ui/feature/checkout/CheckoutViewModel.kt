package com.coffeebean.ui.feature.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Add a data class for a branch
data class Branch(val name: String, val location: LatLng)

sealed class CheckoutUiState {
    object Loading : CheckoutUiState()
    data class Success(
        val userLocation: LatLng,
        val nearestBranches: List<Branch>,
        val selectedBranch: Branch? = null,
        val isBottomSheetVisible: Boolean = false
    ) : CheckoutUiState()
    data class Error(val message: String) : CheckoutUiState()
}

// Add a sealed interface for one-off events
sealed interface CheckoutEvent {
    data class ShowSnackbar(val message: String) : CheckoutEvent
}

@HiltViewModel
class CheckoutViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<CheckoutEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            delay(1500)
            val userLocation = LatLng(14.58, 121.05)
            val nearestBranches = listOf(
                Branch("Coffee Bean @ Ayala Center, Makati", LatLng(14.5547, 121.0282)),
                Branch("Coffee Bean @ Shangri-La Plaza, Mandaluyong", LatLng(14.5815, 121.0544)),
                Branch("Coffee Bean @ SM Megamall, Ortigas", LatLng(14.5855, 121.0593))
            )
            _uiState.value = CheckoutUiState.Success(userLocation, nearestBranches)
        }
    }

    fun onBranchSelected(branch: Branch) {
        _uiState.update {
            if (it is CheckoutUiState.Success) {
                it.copy(selectedBranch = branch, isBottomSheetVisible = true)
            } else {
                it
            }
        }
    }

    fun onBottomSheetDismissed() {
        _uiState.update {
            if (it is CheckoutUiState.Success) {
                it.copy(isBottomSheetVisible = false)
            } else {
                it
            }
        }
    }

    fun onConfirmPickup() {
        viewModelScope.launch {
            if (_uiState.value is CheckoutUiState.Success) {
                val branchName = (_uiState.value as CheckoutUiState.Success).selectedBranch?.name
                _eventFlow.emit(CheckoutEvent.ShowSnackbar("Pickup confirmed at $branchName!"))
            }
        }
    }
}
