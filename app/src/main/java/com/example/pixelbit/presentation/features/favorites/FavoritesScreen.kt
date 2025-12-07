package com.example.pixelbit.presentation.features.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pixelbit.R
import com.example.pixelbit.domain.model.Product
import com.example.pixelbit.presentation.features.products.ProductItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = koinViewModel(),
    onProductClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.my_favorites),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        contentWindowInsets = WindowInsets(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        FavoritesContent(
            uiState = uiState,
            onRemoveFromFavorites = viewModel::removeFromFavorites,
            onProductClick = onProductClick,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun FavoritesContent(
    uiState: FavoritesUiState,
    onRemoveFromFavorites: (String) -> Unit,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.favoriteProducts.isEmpty() -> {
                EmptyFavoritesView(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(150.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 24.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.favoriteProducts,
                        key = { it.id }
                    ) { product ->
                        ProductItem(
                            product = product,
                            onFavoriteClick = { onRemoveFromFavorites(product.id) },
                            onClick = { onProductClick(product.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = stringResource(id = R.string.no_favorites_description),
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.no_favorites_yet),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.start_adding_favorites),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    MaterialTheme {
        FavoritesContent(
            uiState = FavoritesUiState(
                favoriteProducts = listOf(
                    Product(
                        id = "200",
                        title = "Samsung A54",
                        price = "330.00",
                        category = "Smartphones",
                        brand = "bradn",
                        images = "",
                        description = "",
                        isFavorite = true,
                    )
                ),
                isLoading = false,
                errorMessage = null

            ),
            onRemoveFromFavorites = {},
            onProductClick = {}
        )
    }
}
