package com.coffeebean.ui.feature.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    var active by remember { mutableStateOf(false) }

    SearchBar(
        shadowElevation = 8.dp,
        query = searchText,
        onQueryChange = { onSearchTextChange(it) },
        onSearch = { onSearch(it) },
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text("Search for your favorite coffee") },
        trailingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp)

    )
    {
        // Optional dropdown results when active
        Text(
            "Suggested Item 1",
            modifier = Modifier.padding(16.dp)
        )
        Text(
            "Suggested Item 2",
            modifier = Modifier.padding(16.dp)
        )
    }
}
