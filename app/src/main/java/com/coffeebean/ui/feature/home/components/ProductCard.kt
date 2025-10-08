package com.coffeebean.ui.feature.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coffeebean.domain.model.Product
import com.coffeebean.ui.theme.coffeebeanPrice
import com.coffeebean.ui.theme.coffeebeanPurple

@Composable
fun ProductCard(product: Product) {
    Card(
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier

            .width(160.dp)
            .height(180.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            Column(Modifier.padding(8.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, color = coffeebeanPurple)
                Text(product.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(product.price, fontWeight = FontWeight.Bold, color = coffeebeanPrice)
            }
        }
    }
}