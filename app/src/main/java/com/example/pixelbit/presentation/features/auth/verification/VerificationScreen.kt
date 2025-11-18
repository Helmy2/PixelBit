package com.example.pixelbit.presentation.features.auth.verification

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pixelbit.ui.theme.PixelbitTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    email: String,
    onVerificationSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: VerificationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    val onCheckVerification = remember(viewModel) { { viewModel.checkVerification() } }
    val onResendEmail = remember(viewModel) { { viewModel.sendVerificationEmail() } }

    val handleBack = remember(viewModel) {
        {
            viewModel.deleteUserAndSignOut()
        }
    }


    BackHandler(enabled = !state.isDeleting) {
        handleBack()
    }

    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    LaunchedEffect(state.isVerified) {
        if (state.isVerified) {
            onVerificationSuccess()
        }
    }

    LaunchedEffect(state.deletionComplete) {
        if (state.deletionComplete) {
            onBack()
        }
    }

    VerificationScreenContent(
        email = email,
        state = state,
        onCheckVerification = onCheckVerification,
        onResendEmail = onResendEmail,
        onBack = handleBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreenContent(
    email: String,
    state: VerificationState,
    onCheckVerification: () -> Unit,
    onResendEmail: () -> Unit,
    onBack: () -> Unit
) {
    val isButtonEnabled by remember(state.isLoading, state.isDeleting) {
        derivedStateOf { !state.isLoading && !state.isDeleting }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Email Verification") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email Verification",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(bottom = 24.dp),
                    tint = Color(0xFF514EB7)
                )

                Text(
                    text = "Verify Your Email",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "We've sent a verification link to:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = email,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Text(
                    text = "Please check your email and click the verification link. After verifying, click the button below to continue.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onCheckVerification,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isButtonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF514EB7),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "I've Verified My Email",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (state.canResend) {
                    TextButton(
                        onClick = onResendEmail,
                        enabled = isButtonEnabled
                    ) {
                        Text(
                            "Resend Verification Email",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Surface(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Resend available in ${state.resendCountdown}s",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = state.errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isButtonEnabled
                ) {
                    Text(
                        "Back to Sign In",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Didn't receive the email? Check your spam folder or try resending.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
        }
    }
}

@Preview(
    name = "Verification",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun VerificationScreenPreview() {
    PixelbitTheme {
        VerificationScreenContent(
            email = "Ibrahim@gmail.com",
            state = VerificationState(
                resendCountdown = 45,
                canResend = false,
                verificationEmailSent = true
            ),
            onCheckVerification = {},
            onResendEmail = {},
            onBack = {}
        )
    }
}
