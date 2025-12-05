package com.example.pixelbit.presentation.features.address

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.pixelbit.R
import com.example.pixelbit.domain.model.Address
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    viewModel: AddressViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.manage_address)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onBackClicked() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_arrow)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onAddAddressClicked() }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_address)
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.errorMessage ?: stringResource(id = R.string.an_error_occurred))
            }
        } else if (uiState.addresses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(id = R.string.no_addresses_found))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.addresses) { address ->
                    AddressCard(address, viewModel)
                }
            }
        }

        if (uiState.isDialogShown) {
            AddressDialog(
                address = uiState.selectedAddress,
                onDismiss = { viewModel.onDialogDismissed() },
                onSave = { viewModel.onAddressSaved(it) }
            )
        }
    }
}

@Composable
fun AddressCard(address: Address, viewModel: AddressViewModel) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "${address.street}, ${address.city}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (address.default) {
                    Text(
                        text = stringResource(id = R.string.default_address),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    TextButton(onClick = { viewModel.onSetDefaultAddressClicked(address.id) }) {
                        Text(stringResource(id = R.string.set_as_default))
                    }
                }

                Spacer(Modifier.weight(1f))

                IconButton(onClick = { viewModel.onEditAddressClicked(address) }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(id = R.string.edit_address)
                    )
                }
                IconButton(onClick = { viewModel.onDeleteAddressClicked(address.id) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.delete_address)
                    )
                }
            }
        }
    }
}

@Composable
fun AddressDialog(address: Address?, onDismiss: () -> Unit, onSave: (Address) -> Unit) {
    var street by remember { mutableStateOf(address?.street ?: "") }
    var city by remember { mutableStateOf(address?.city ?: "") }
    var isDefault by remember { mutableStateOf(address?.default ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(id = if (address == null) R.string.add_address else R.string.edit_address),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text(stringResource(id = R.string.street)) })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text(stringResource(id = R.string.city)) })
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
                    Text(stringResource(id = R.string.set_as_default))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    TextButton(onClick = onDismiss) { Text(stringResource(id = R.string.cancel)) }
                    Button(onClick = {
                        val newAddress = address?.copy(
                            street = street,
                            city = city,
                            default = isDefault
                        ) ?: Address(
                            street = street,
                            city = city,
                            default = isDefault
                        )
                        onSave(newAddress)
                    }) { Text(stringResource(id = R.string.save)) }
                }
            }
        }
    }
}
