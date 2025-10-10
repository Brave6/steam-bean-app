package com.coffeebean.ui.feature.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.coffeebean.domain.model.Product
import com.coffeebean.ui.theme.coffeebeanPrice
import com.coffeebean.ui.theme.coffeebeanPurple
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductCard(product: Product) {
    // Formatter for currency
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH"))

    Card(
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .width(160.dp)
            .height(190.dp) // Slightly increased height for better spacing
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp) // Adjusted image height
            )
            Column(Modifier.padding(10.dp)) { // Increased padding
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    color = coffeebeanPurple,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = currencyFormat.format(product.price),
                    fontWeight = FontWeight.Bold,
                    color = coffeebeanPrice,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
