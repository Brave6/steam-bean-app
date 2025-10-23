// ========== CheckoutScreen.kt ==========
// File: ui/feature/checkout/CheckoutScreen.kt
package com.coffeebean.ui.feature.checkout

import android.Manifest
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.coffeebean.domain.model.Branch
import com.coffeebean.domain.model.FulfillmentType
import com.coffeebean.domain.model.PaymentMethod
import com.coffeebean.ui.theme.Recolleta
import com.coffeebean.ui.theme.coffeebeanPrice
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CheckoutScreen(
    navController: NavHostController,
    onNavigateBack: () -> Unit,
    onOrderSuccess: (String) -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Location permissions
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Get user's current location
    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        viewModel.updateUserLocation(LatLng(it.latitude, it.longitude))
                    }
                }
            } catch (e: SecurityException) {
                // Handle permission error
            }
        }
    }

    // Map Dialog for address selection
    if (showMapDialog) {
        DeliveryMapDialog(
            userLocation = userLocation ?: LatLng(14.5995, 120.9842), // Default Manila
            onDismiss = { showMapDialog = false },
            onConfirm = { deliveryAddress ->
                onAddressSelected(deliveryAddress)
                showMapDialog = false
            }
        )
    }
}

@Composable
private fun BranchSelectionSection(
    branches: List<Branch>,
    selectedBranch: Branch?,
    nearestBranch: Branch?,
    userLocation: LatLng?,
    onBranchSelected: (Branch) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Select Branch",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF532D6D)
            )

            if (nearestBranch != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Nearest: ${nearestBranch.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Branch list
            branches.forEach { branch ->
                BranchCard(
                    branch = branch,
                    isSelected = selectedBranch?.id == branch.id,
                    isNearest = nearestBranch?.id == branch.id,
                    userLocation = userLocation,
                    onClick = { onBranchSelected(branch) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun BranchCard(
    branch: Branch,
    isSelected: Boolean,
    isNearest: Boolean,
    userLocation: LatLng?,
    onClick: () -> Unit
) {
    val distance = remember(userLocation) {
        if (userLocation != null) {
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                userLocation.latitude, userLocation.longitude,
                branch.latitude, branch.longitude,
                results
            )
            (results[0] / 1000).let { "%.1f km".format(it) }
        } else null
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF532D6D).copy(alpha = 0.1f) else Color(0xFFF5F5F5)
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, Color(0xFF532D6D))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Branch icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF532D6D).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Store,
                    contentDescription = null,
                    tint = Color(0xFF532D6D)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = branch.name.replace("The Coffee Bean - ", ""),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF532D6D)
                    )
                    if (isNearest) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "NEAREST",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = branch.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (distance != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DirectionsCar,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = distance,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = branch.operatingHours,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color(0xFF532D6D)
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodSection(
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Payment Method",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF532D6D)
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentMethod.values().forEach { method ->
                PaymentMethodItem(
                    method = method,
                    isSelected = selectedMethod == method,
                    onClick = { onMethodSelected(method) }
                )
                if (method != PaymentMethod.values().last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodItem(
    method: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (icon, label) = when (method) {
        PaymentMethod.CASH -> Icons.Default.Money to "Cash on Delivery"
        PaymentMethod.GCASH -> Icons.Default.AccountBalance to "GCash"
        PaymentMethod.CARD -> Icons.Default.CreditCard to "Credit/Debit Card"
    }

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = if (isSelected) Color(0xFF532D6D).copy(alpha = 0.1f) else Color(0xFFF5F5F5),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) BorderStroke(2.dp, Color(0xFF532D6D)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color(0xFF532D6D) else Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFF532D6D) else Color.Gray,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color(0xFF532D6D)
                )
            }
        }
    }
}

@Composable
private fun OrderSummarySection(state: CheckoutUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Order Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF532D6D)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Items
            state.cartItems.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.quantity}x ${item.productName}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatPrice(item.totalPrice),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Subtotal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                Text(
                    formatPrice(state.subtotal),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Delivery Fee
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Delivery Fee", style = MaterialTheme.typography.bodyMedium)
                if (state.deliveryFee == 0.0 && state.fulfillmentType == FulfillmentType.DELIVERY) {
                    Text(
                        "FREE",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        formatPrice(state.deliveryFee),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF532D6D)
                )
                Text(
                    formatPrice(state.total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = coffeebeanPrice
                )
            }
        }
    }
}

@Composable
private fun CheckoutBottomBar(
    state: CheckoutUiState,
    onPlaceOrder: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Button(
            onClick = onPlaceOrder,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            enabled = !state.isPlacingOrder && isOrderValid(state),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF532D6D),
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (state.isPlacingOrder) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Place Order • ${formatPrice(state.total)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DeliveryMapDialog(
    userLocation: LatLng,
    onDismiss: () -> Unit,
    onConfirm: (com.coffeebean.domain.model.DeliveryAddress) -> Unit
) {
    var selectedLocation by remember { mutableStateOf(userLocation) }
    var addressText by remember { mutableStateOf("") }
    var landmarkText by remember { mutableStateOf("") }
    var instructionsText by remember { mutableStateOf("") }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 15f)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Select Delivery Location", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                // Google Map
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { selectedLocation = it }
                    ) {
                        Marker(
                            state = MarkerState(position = selectedLocation),
                            title = "Delivery Here"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Address input
                OutlinedTextField(
                    value = addressText,
                    onValueChange = { addressText = it },
                    label = { Text("Full Address") },
                    placeholder = { Text("Street, Barangay, City") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = landmarkText,
                    onValueChange = { landmarkText = it },
                    label = { Text("Landmark (Optional)") },
                    placeholder = { Text("Near McDonald's") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = instructionsText,
                    onValueChange = { instructionsText = it },
                    label = { Text("Delivery Instructions (Optional)") },
                    placeholder = { Text("Ring doorbell, leave at gate, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (addressText.isNotBlank()) {
                        onConfirm(
                            com.coffeebean.domain.model.DeliveryAddress(
                                fullAddress = addressText,
                                landmark = landmarkText,
                                latitude = selectedLocation.latitude,
                                longitude = selectedLocation.longitude,
                                instructions = instructionsText
                            )
                        )
                    }
                },
                enabled = addressText.isNotBlank()
            ) {
                Text("Confirm Location", color = Color(0xFF532D6D))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = Color(0xFF532D6D)
        )
    }
}

private fun isOrderValid(state: CheckoutUiState): Boolean {
    return when (state.fulfillmentType) {
        FulfillmentType.DELIVERY -> state.deliveryAddress != null
        FulfillmentType.PICKUP -> state.selectedBranch != null
    }
}

private fun formatPrice(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "PH"))
    return format.format(price)
}

// Show error snackbar
val snackbarHostState = remember { SnackbarHostState() }
LaunchedEffect(uiState.error) {
    uiState.error?.let {
        snackbarHostState.showSnackbar(it)
        viewModel.clearError()
    }
}

Scaffold(
topBar = {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Checkout",
                fontFamily = Recolleta,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            navigationIconContentColor = Color(0xFF532D6D),
            titleContentColor = Color(0xFF532D6D)
        )
    )
},
snackbarHost = { SnackbarHost(snackbarHostState) },
bottomBar = {
    CheckoutBottomBar(
        state = uiState,
        onPlaceOrder = {
            viewModel.placeOrder(onSuccess = onOrderSuccess)
        }
    )
}
) { padding ->
    if (uiState.isLoading) {
        LoadingContent()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fulfillment Type Selector
            item {
                FulfillmentTypeSelector(
                    selectedType = uiState.fulfillmentType,
                    onTypeSelected = viewModel::setFulfillmentType
                )
            }

            // Delivery Address or Branch Selection
            item {
                AnimatedContent(
                    targetState = uiState.fulfillmentType,
                    label = "fulfillment_animation"
                ) { type ->
                    when (type) {
                        FulfillmentType.DELIVERY -> {
                            DeliveryAddressSection(
                                address = uiState.deliveryAddress,
                                userLocation = uiState.userLocation,
                                locationPermissions = locationPermissions,
                                onAddressSelected = viewModel::updateDeliveryAddress
                            )
                        }
                        FulfillmentType.PICKUP -> {
                            BranchSelectionSection(
                                branches = uiState.branches,
                                selectedBranch = uiState.selectedBranch,
                                nearestBranch = uiState.nearestBranch,
                                userLocation = uiState.userLocation,
                                onBranchSelected = viewModel::selectBranch
                            )
                        }
                    }
                }
            }

            // Payment Method
            item {
                PaymentMethodSection(
                    selectedMethod = uiState.paymentMethod,
                    onMethodSelected = viewModel::setPaymentMethod
                )
            }

            // Order Summary
            item {
                OrderSummarySection(state = uiState)
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
}

@Composable
private fun FulfillmentTypeSelector(
    selectedType: FulfillmentType,
    onTypeSelected: (FulfillmentType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "How would you like to receive your order?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF532D6D)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FulfillmentTypeCard(
                    type = FulfillmentType.DELIVERY,
                    icon = Icons.Default.LocalShipping,
                    label = "Delivery",
                    isSelected = selectedType == FulfillmentType.DELIVERY,
                    onClick = { onTypeSelected(FulfillmentType.DELIVERY) },
                    modifier = Modifier.weight(1f)
                )

                FulfillmentTypeCard(
                    type = FulfillmentType.PICKUP,
                    icon = Icons.Default.Store,
                    label = "Pick Up",
                    isSelected = selectedType == FulfillmentType.PICKUP,
                    onClick = { onTypeSelected(FulfillmentType.PICKUP) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FulfillmentTypeCard(
    type: FulfillmentType,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF532D6D).copy(alpha = 0.1f) else Color.White
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, Color(0xFF532D6D))
        } else {
            BorderStroke(1.dp, Color.LightGray)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) Color(0xFF532D6D) else Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFF532D6D) else Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun DeliveryAddressSection(
    address: com.coffeebean.domain.model.DeliveryAddress?,
    userLocation: LatLng?,
    locationPermissions: com.google.accompanist.permissions.MultiplePermissionsState,
    onAddressSelected: (com.coffeebean.domain.model.DeliveryAddress) -> Unit
) {
    var showMapDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Delivery Address",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF532D6D)
                )

                if (!locationPermissions.allPermissionsGranted) {
                    TextButton(
                        onClick = { locationPermissions.launchMultiplePermissionRequest() }
                    ) {
                        Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Enable Location")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (address != null) {
                // Show selected address
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                        .clickable { showMapDialog = true }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF532D6D)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = address.fullAddress,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (address.landmark.isNotEmpty()) {
                            Text(
                                text = "Near: ${address.landmark}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                }
            } else {
                // Show map picker button
                OutlinedButton(
                    onClick = { showMapDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = locationPermissions.allPermissionsGranted
                ) {
                    Icon(Icons.Default.Map, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pin your location on map")
                }
            }
        }
    }