package com.coffeebean.ui.feature.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuCategorySection(
    title: String,
    items: List<MenuItem>,
    onItemClick: (String) -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(items) { item ->
            MenuCard(item, onItemClick as () -> Unit)
        }
    }
}
