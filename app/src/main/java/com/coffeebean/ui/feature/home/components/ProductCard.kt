package com.coffeebean.ui.feature.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.coffeebean.domain.model.Product
import com.coffeebean.ui.theme.coffeebeanPrice
import com.coffeebean.ui.theme.coffeebeanPurple
import java.text.NumberFormat
import java.util.Locale

/**
 * Product card component with shimmer loading effect and best practices.
 *
 * Features:
 * - Shimmer loading animation while image loads
 * - Error state handling
 * - Proper accessibility support
 * - Optimized image loading with Coil
 * - Currency formatting
 * - Click support with optional callback
 *
 * @param product The product to display
 * @param onClick Optional callback when card is clicked
 * @param modifier Optional modifier for the card
 */
@Composable
fun ProductCard(
    product: Product,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Memoize currency formatter to avoid recreation on recomposition
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("en", "PH"))
    }

    Card(
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .width(160.dp)
            .height(180.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .semantics {
                contentDescription = "Product: ${product.name}, ${product.description}, " +
                        "Price: ${currencyFormat.format(product.price)}"
            }
    ) {
        Column {
            // Product Image with shimmer loading
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null, // Description is on the Card
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                loading = {
                    ProductImageShimmer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                },
                error = {
                    ProductImageError(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                }
            )

            // Product Details
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                // Product Name
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    color = coffeebeanPurple,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Product Description
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
                // Product Price
                Text(
                    text = currencyFormat.format(product.price),
                    fontWeight = FontWeight.Bold,
                    color = coffeebeanPrice,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * Shimmer loading effect for product images
 */
@Composable
private fun ProductImageShimmer(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "product_shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        Color(0xFFE0E0E0),
        Color(0xFFF5F5F5),
        Color(0xFFE0E0E0)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 1000f, translateAnim - 1000f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier.background(brush)
    )
}

/**
 * Error state for failed image loading
 */
@Composable
private fun ProductImageError(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        // You can add an error icon here if desired
        Text(
            text = "Image unavailable",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

/**
 * Loading shimmer for the entire product card (optional - for loading states)
 */
@Composable
fun ProductCardShimmer(modifier: Modifier = Modifier) {
    Card(
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .width(160.dp)
            .height(220.dp)
    ) {
        Column {
            // Image shimmer
            ProductImageShimmer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            // Text shimmer placeholders
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                // Title shimmer
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description shimmer
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                )
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(12.dp)
                        .padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Price shimmer
                ShimmerBox(
                    modifier = Modifier
                        .width(60.dp)
                        .height(16.dp)
                )
            }
        }
    }
}

/**
 * Generic shimmer box for text placeholders
 */
@Composable
private fun ShimmerBox(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "box_shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        Color(0xFFE0E0E0),
        Color(0xFFF5F5F5),
        Color(0xFFE0E0E0)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 1000f, translateAnim - 1000f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .background(brush, RoundedCornerShape(4.dp))
    )
}