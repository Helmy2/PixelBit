package com.example.pixelbit.presentation.features.checkout

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pixelbit.R
import com.example.pixelbit.domain.model.CartItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.checkout)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onBackClicked() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_arrow)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.errorMessage != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = uiState.errorMessage ?: stringResource(id = R.string.an_error_occurred))
                Button(onClick = { viewModel.clearError() }) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        } else {
            CheckoutContent(
                uiState = uiState,
                viewModel = viewModel,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(paddingValues)
            )
        }
    }
}

@Composable
private fun CheckoutContent(
    uiState: CheckoutUiState,
    viewModel: CheckoutViewModel,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.orderPlaced) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.order_placed_successfully),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.onBackClicked() }) {
                    Text(stringResource(id = R.string.continue_shopping))
                }
            }
        } else if (uiState.checkoutItems.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.your_cart_is_empty),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.onBackClicked() }) {
                    Text(stringResource(id = R.string.continue_shopping))
                }
            }
        } else {
            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.checkoutItems) { item ->
                            CheckoutItem(item = item, isLandscape = true)
                            HorizontalDivider()
                        }
                    }

                    Column(
                        modifier = Modifier
                            .width(340.dp)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        PriceSummary(
                            subtotal = uiState.subtotal,
                            shipping = uiState.shipping,
                            total = uiState.total,
                            isLandscape = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.placeOrder() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isPlacingOrder
                        ) {
                            if (uiState.isPlacingOrder) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(stringResource(id = R.string.place_order))
                            }
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(uiState.checkoutItems) { item ->
                            CheckoutItem(item = item, isLandscape = false)
                            HorizontalDivider()
                        }
                    }

                    PriceSummary(
                        subtotal = uiState.subtotal,
                        shipping = uiState.shipping,
                        total = uiState.total,
                        isLandscape = false
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.placeOrder() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isPlacingOrder
                    ) {
                        if (uiState.isPlacingOrder) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(stringResource(id = R.string.place_order))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutItem(item: CartItem, isLandscape: Boolean) {
    val imageSize = if (isLandscape) 80.dp else 64.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.images,
            contentDescription = item.title,
            modifier = Modifier.size(imageSize)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
            Text(text = item.brand, style = MaterialTheme.typography.bodySmall)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(id = R.string.price_format, item.getTotalPrice()),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(id = R.string.quantity_format, item.quantity),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun PriceSummary(subtotal: Double, shipping: Double, total: Double, isLandscape: Boolean) {
    val cardElevation = if (isLandscape) 2.dp else 0.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(id = R.string.subtotal),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(id = R.string.price_format, subtotal),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(id = R.string.shipping),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(id = R.string.price_format, shipping),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.price_format, total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}