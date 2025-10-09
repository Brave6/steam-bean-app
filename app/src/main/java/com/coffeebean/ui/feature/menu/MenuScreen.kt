package com.coffeebean.ui.feature.menu

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coffeebean.ui.theme.Recolleta

/**
 * Menu category enum for type-safe tab management
 */
enum class MenuCategory(val displayName: String) {
    COFFEE("Coffee"),
    BEANS("Beans"),
    CAKES("Cakes"),
    PASTRY("Pastry")
}

/**
 * UI state for menu screen
 */
sealed class MenuUiState {
    data object Loading : MenuUiState()
    data class Success(val items: Map<MenuCategory, List<MenuItem>>) : MenuUiState()
    data class Error(val message: String) : MenuUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    navController: NavHostController,
    onItemClick: (MenuItem) -> Unit,
    onSearchClick: () -> Unit = {},
    viewModel: MenuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val categories = MenuCategory.entries

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Menu",
                        fontFamily = Recolleta,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.semantics {
                            contentDescription = "Navigate back"
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier.semantics {
                            contentDescription = "Search menu items"
                        }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = Color(0xFF532D6D),
                    titleContentColor = Color(0xFF532D6D),
                    actionIconContentColor = Color(0xFF532D6D)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Category Tabs
            MenuTabs(
                categories = categories,
                selectedIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )

            // Content based on state
            when (val state = uiState) {
                is MenuUiState.Loading -> LoadingContent()
                is MenuUiState.Success -> {
                    MenuContent(
                        selectedCategory = categories[selectedTabIndex],
                        menuItems = state.items,
                        onItemClick = onItemClick
                    )
                }
                is MenuUiState.Error -> ErrorContent(message = state.message)
            }
        }
    }
}

@Composable
private fun MenuTabs(
    categories: List<MenuCategory>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            if (selectedIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = category.displayName,
                        color = if (selectedIndex == index)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = if (selectedIndex == index)
                            FontWeight.SemiBold
                        else
                            FontWeight.Normal
                    )
                },
                modifier = Modifier.semantics {
                    contentDescription = "${category.displayName} category tab"
                }
            )
        }
    }
}

@Composable
private fun MenuContent(
    selectedCategory: MenuCategory,
    menuItems: Map<MenuCategory, List<MenuItem>>,
    onItemClick: (MenuItem) -> Unit
) {
    val items = menuItems[selectedCategory] ?: emptyList()

    AnimatedContent(
        targetState = selectedCategory,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "menu_content_animation"
    ) { category ->
        if (items.isEmpty()) {
            EmptyContent(category = category.displayName)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Group items by subcategory
                val groupedItems = items.groupBy { it.subcategory }

                groupedItems.forEach { (subcategory, subItems) ->
                    if (subcategory != null) {
                        item(key = "header_$subcategory") {
                            SubcategoryHeader(subcategory)
                        }
                    }

                    items(
                        items = subItems,
                        key = { it.id.ifEmpty { it.name } }
                    ) { item ->
                        MenuCard(
                            item = item,
                            onClick = { onItemClick(item) }
                        )
                    }

                    item(key = "spacer_$subcategory") {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Bottom spacing for last item
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun SubcategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
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
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun EmptyContent(category: String) {
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
                text = "No items available",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Check back soon for $category items!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}
/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MenuScreenPreview() {
    val navController = rememberNavController()
    // In preview, we can provide a dummy implementation of the view model
    // or pass a dummy uiState directly if the composable supports it.
    // For this preview, let's keep it simple and assume a success state.
    val dummyItems = mapOf(
        MenuCategory.COFFEE to listOf(
            MenuItem(id = "1", name = "Cappuccino", description = "with Chocolate", price = 125.00, imageUrl = "", category = MenuCategory.COFFEE, subcategory = "Hot Coffee"),
            MenuItem(id = "2", name = "Caramel Latte", description = "with Oat Milk", price = 150.00, imageUrl = "", category = MenuCategory.COFFEE, subcategory = "Hot Coffee"),
        ),
        MenuCategory.BEANS to emptyList(),
        MenuCategory.CAKES to emptyList(),
        MenuCategory.PASTRY to emptyList()
    )

    // This is a simplified approach for the preview.
    // In a real app, you might use a fake ViewModel that provides this state.
    MenuScreen(
        navController = navController,
        onItemClick = {},
        viewModel = object : MenuViewModel(object : com.coffeebean.domain.repository.MenuRepository {
            override suspend fun getProducts(): List<com.coffeebean.domain.model.Product> {
                return emptyList()
            }
        }) {
            override val uiState: kotlinx.coroutines.flow.StateFlow<MenuUiState> =
                kotlinx.coroutines.flow.MutableStateFlow(MenuUiState.Success(dummyItems))
        }
    )
}

 */