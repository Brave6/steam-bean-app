package com.coffeebean.ui.feature.menu.components.product

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.coffeebean.ui.theme.Recolleta
import com.coffeebean.ui.theme.coffeebeanPrice
import java.text.NumberFormat
import java.util.Locale

/**
 * Product Detail UI State
 */
sealed class ProductDetailUiState {
    data object Loading : ProductDetailUiState()
    data class Success(
        val product: ProductDetail,
        val quantity: Int = 1,
        val selectedSize: ProductSize? = null,
        val selectedTemperature: ProductTemperature? = null,
        val isFavorite: Boolean = false,
        val isAddingToCart: Boolean = false
    ) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}

/**
 * Product Detail Data Class
 */
data class ProductDetail(
    val id: String,
    val name: String,
    val description: String,
    val longDescription: String? = null,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val subcategory: String? = null,
    val available: Boolean = true,
    val sizes: List<ProductSize> = emptyList(),
    val temperatures: List<ProductTemperature> = emptyList(),
    val nutritionInfo: NutritionInfo? = null,
    val allergens: List<String> = emptyList()
)

data class ProductSize(
    val id: String,
    val name: String, // "Small", "Medium", "Large"
    val priceModifier: Double = 0.0 // Additional cost
)

data class ProductTemperature(
    val id: String,
    val name: String // "Hot", "Iced", "Blended"
)

data class NutritionInfo(
    val calories: Int,
    val caffeine: Int? = null,
    val sugar: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    
    // Parallax effect for image
    val imageScale by animateFloatAsState(
        targetValue = 1f + (scrollState.value / 1000f).coerceIn(0f, 0.3f),
        label = "image_scale"
    )

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    Scaffold(
        topBar = {
            ProductDetailTopBar(
                onNavigateBack = onNavigateBack,
                scrollState = scrollState
            )
        },
        bottomBar = {
            if (uiState is ProductDetailUiState.Success) {
                AddToCartBottomBar(
                    state = uiState as ProductDetailUiState.Success,
                    onQuantityChange = viewModel::updateQuantity,
                    onAddToCart = {
                        viewModel.addToCart(
                            onSuccess = { onNavigateToCart() }
                        )
                    }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is ProductDetailUiState.Loading -> {
                    LoadingContent()
                }
                is ProductDetailUiState.Success -> {
                    ProductDetailContent(
                        state = state,
                        imageScale = imageScale,
                        scrollState = scrollState,
                        onSizeSelected = viewModel::selectSize,
                        onTemperatureSelected = viewModel::selectTemperature,
                        onFavoriteToggle = viewModel::toggleFavorite,
                        modifier = Modifier.padding(padding)
                    )
                }
                is ProductDetailUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.loadProduct(productId) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailTopBar(
    onNavigateBack: () -> Unit,
    scrollState: ScrollState
) {
    val isScrolled = scrollState.value > 100
    
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        color = if (isScrolled) Color.Transparent else Color.White.copy(alpha = 0.9f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    tint = Color(0xFF532D6D)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isScrolled) Color.White else Color.Transparent,
            scrolledContainerColor = Color.White
        )
    )
}

@Composable
private fun ProductDetailContent(
    state: ProductDetailUiState.Success,
    imageScale: Float,
    scrollState: ScrollState,
    onSizeSelected: (ProductSize) -> Unit,
    onTemperatureSelected: (ProductTemperature) -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Product Image with Parallax
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(state.product.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = state.product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = imageScale
                        scaleY = imageScale
                    }
            )
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f)
                            ),
                            startY = 300f
                        )
                    )
            )
            
            // Favorite button
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(
                    imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (state.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (state.isFavorite) Color.Red else Color.Gray
                )
            }
        }

        // Product Information
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(24.dp)
        ) {
            // Category badge
            if (state.product.subcategory != null) {
                Text(
                    text = state.product.subcategory,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF532D6D).copy(alpha = 0.7f),
                    modifier = Modifier
                        .background(
                            color = Color(0xFF532D6D).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Product name
            Text(
                text = state.product.name,
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = Recolleta,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF532D6D)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Short description
            Text(
                text = state.product.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Price
            Text(
                text = formatPrice(calculateTotalPrice(state)),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = coffeebeanPrice
            )

            // Size selection
            if (state.product.sizes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                SizeSelector(
                    sizes = state.product.sizes,
                    selectedSize = state.selectedSize,
                    onSizeSelected = onSizeSelected
                )
            }

            // Temperature selection
            if (state.product.temperatures.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                TemperatureSelector(
                    temperatures = state.product.temperatures,
                    selectedTemperature = state.selectedTemperature,
                    onTemperatureSelected = onTemperatureSelected
                )
            }

            // Long description
            if (!state.product.longDescription.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "About this product",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF532D6D)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.product.longDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }

            // Nutrition info
            if (state.product.nutritionInfo != null) {
                Spacer(modifier = Modifier.height(24.dp))
                NutritionInfoSection(nutritionInfo = state.product.nutritionInfo)
            }

            // Allergens
            if (state.product.allergens.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                AllergensSection(allergens = state.product.allergens)
            }

            // Bottom spacing for cart button
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun SizeSelector(
    sizes: List<ProductSize>,
    selectedSize: ProductSize?,
    onSizeSelected: (ProductSize) -> Unit
) {
    Column {
        Text(
            text = "Size",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF532D6D)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            sizes.forEach { size ->
                SizeChip(
                    size = size,
                    isSelected = selectedSize?.id == size.id,
                    onClick = { onSizeSelected(size) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SizeChip(
    size: ProductSize,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) Color(0xFF532D6D) else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color(0xFF532D6D)
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 2.dp,
            brush = if (isSelected) 
                Brush.linearGradient(listOf(Color(0xFF532D6D), Color(0xFF532D6D)))
            else 
                Brush.linearGradient(listOf(Color.LightGray, Color.LightGray))
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = size.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (size.priceModifier > 0) {
                Text(
                    text = "+${formatPrice(size.priceModifier)}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun TemperatureSelector(
    temperatures: List<ProductTemperature>,
    selectedTemperature: ProductTemperature?,
    onTemperatureSelected: (ProductTemperature) -> Unit
) {
    Column {
        Text(
            text = "Temperature",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF532D6D)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            temperatures.forEach { temp ->
                TemperatureChip(
                    temperature = temp,
                    isSelected = selectedTemperature?.id == temp.id,
                    onClick = { onTemperatureSelected(temp) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TemperatureChip(
    temperature: ProductTemperature,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) Color(0xFF532D6D) else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color(0xFF532D6D)
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 2.dp,
            brush = if (isSelected) 
                Brush.linearGradient(listOf(Color(0xFF532D6D), Color(0xFF532D6D)))
            else 
                Brush.linearGradient(listOf(Color.LightGray, Color.LightGray))
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = temperature.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun NutritionInfoSection(nutritionInfo: NutritionInfo) {
    Column {
        Text(
            text = "Nutrition Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF532D6D)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NutritionItem(label = "Calories", value = "${nutritionInfo.calories} kcal")
            if (nutritionInfo.caffeine != null) {
                NutritionItem(label = "Caffeine", value = "${nutritionInfo.caffeine} mg")
            }
            if (nutritionInfo.sugar != null) {
                NutritionItem(label = "Sugar", value = "${nutritionInfo.sugar} g")
            }
        }
    }
}

@Composable
private fun NutritionItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF532D6D)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun AllergensSection(allergens: List<String>) {
    Column {
        Text(
            text = "Allergen Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF532D6D)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Contains: ${allergens.joinToString(", ")}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        )
    }
}

@Composable
private fun AddToCartBottomBar(
    state: ProductDetailUiState.Success,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quantity selector
            Row(
                modifier = Modifier
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (state.quantity > 1) onQuantityChange(state.quantity - 1) },
                    enabled = state.quantity > 1
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Decrease quantity",
                        tint = if (state.quantity > 1) Color(0xFF532D6D) else Color.Gray
                    )
                }
                
                Text(
                    text = state.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                IconButton(
                    onClick = { onQuantityChange(state.quantity + 1) }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Increase quantity",
                        tint = Color(0xFF532D6D)
                    )
                }
            }

            // Add to cart button
            Button(
                onClick = onAddToCart,
                enabled = !state.isAddingToCart && state.product.available,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF532D6D),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isAddingToCart) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add to Cart",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
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

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF532D6D)
                )
            ) {
                Text("Retry")
            }
        }
    }
}

private fun calculateTotalPrice(state: ProductDetailUiState.Success): Double {
    val basePrice = state.product.price
    val sizeModifier = state.selectedSize?.priceModifier ?: 0.0
    return (basePrice + sizeModifier) * state.quantity
}

private fun formatPrice(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "PH"))
    return format.format(price)
}