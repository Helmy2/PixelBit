package com.example.pixelbit.presentation.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pixelbit.R
import com.example.pixelbit.domain.model.User
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
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
                        text = stringResource(id = R.string.my_profile),
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
        ProfileContent(
            uiState = uiState,
            onLogout = viewModel::signOut,
            onMyOrdersClick = viewModel::onMyOrdersClick,
            onManageAddressClick = viewModel::onManageAddressClick,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onLogout: () -> Unit,
    onMyOrdersClick: () -> Unit,
    onManageAddressClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.user != null -> {
                UserProfile(
                    user = uiState.user,
                    onLogout = onLogout,
                    onMyOrdersClick = onMyOrdersClick,
                    onManageAddressClick = onManageAddressClick
                )
            }
        }
    }
}

@Composable
private fun UserProfile(
    user: User,
    onLogout: () -> Unit,
    onMyOrdersClick: () -> Unit,
    onManageAddressClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .sizeIn(maxWidth = 600.dp)
            .fillMaxSize()
            .padding(16.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileHeader(user)
        AccountInfoCard(user)
        SettingsCard(onMyOrdersClick, onManageAddressClick)
        TextButton(
            onClick = onLogout,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(text = stringResource(id = R.string.logout))
        }
    }
}

@Composable
private fun ProfileHeader(user: User) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                // Todo replace with user image
                .data("")
                .crossfade(true)
                .error(R.drawable.profile_image)
                .build(),
            contentDescription = "",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AccountInfoCard(user: User) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileInfoItem(
                icon = Icons.Default.Email,
                label = stringResource(id = R.string.email_label),
                value = user.email
            )
            HorizontalDivider()
            ProfileInfoItem(
                icon = Icons.Default.Phone,
                label = stringResource(id = R.string.phone_label),
                value = user.phone.ifEmpty { stringResource(id = R.string.not_provided) }
            )
            HorizontalDivider()
            ProfileInfoItem(
                icon = Icons.Default.VerifiedUser,
                label = stringResource(id = R.string.email_verification_label),
                value = if (user.isEmailVerified) stringResource(id = R.string.verified) else stringResource(
                    id = R.string.not_verified
                )
            )
        }
    }
}

@Composable
private fun SettingsCard(
    onMyOrdersClick: () -> Unit,
    onManageAddressClick: () -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column {
            SettingsItem(
                icon = Icons.AutoMirrored.Filled.List,
                text = stringResource(id = R.string.my_orders),
                onClick = onMyOrdersClick
            )
            HorizontalDivider()
            SettingsItem(
                icon = Icons.Default.LocationOn,
                text = stringResource(id = R.string.manage_address),
                onClick = onManageAddressClick
            )
        }
    }
}

@Composable
private fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    ListItem(
        headlineContent = { Text(value) },
        supportingContent = { Text(label) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = label
            )
        }
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = { Text(text) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = text
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileContent(
            uiState = ProfileUiState(
                user = User(
                    uid = "123",
                    name = "John Doe",
                    email = "john.doe@example.com",
                    phone = "123-456-7890",
                    isEmailVerified = true
                ),
                isLoading = false,
                errorMessage = null
            ),
            onLogout = {},
            onMyOrdersClick = {},
            onManageAddressClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenLoadingPreview() {
    MaterialTheme {
        ProfileContent(
            uiState = ProfileUiState(
                isLoading = true
            ),
            onLogout = {},
            onMyOrdersClick = {},
            onManageAddressClick = {}
        )
    }
}
