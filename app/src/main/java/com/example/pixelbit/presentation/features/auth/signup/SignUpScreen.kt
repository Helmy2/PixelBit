package com.example.pixelbit.presentation.features.auth.signup


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pixelbit.ui.theme.PixelbitTheme
import org.koin.androidx.compose.koinViewModel


@Composable
fun SignUpScreen(
    onSignUpSuccess: (String) -> Unit,
    onNavigateToSignIn: () -> Unit,
    viewModel: SignUpViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val onNameChange = remember(viewModel) { { value: String -> viewModel.onNameChange(value) } }
    val onEmailChange = remember(viewModel) { { value: String -> viewModel.onEmailChange(value) } }
    val onPhoneChange = remember(viewModel) { { value: String -> viewModel.onPhoneChange(value) } }
    val onPasswordChange = remember(viewModel) { { value: String -> viewModel.onPasswordChange(value) } }
    val onConfirmPasswordChange = remember(viewModel) { { value: String -> viewModel.onConfirmPasswordChange(value) } }
    val onAgreeToTermsChange = remember(viewModel) { { value: Boolean -> viewModel.onAgreeToTermsChange(value) } }
    val onSignUp = remember(viewModel) { { viewModel.signUp() } }


    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onSignUpSuccess(state.email)
        }
    }

    SignUpScreenContent(
        state = state,
        passwordVisible = passwordVisible,
        confirmPasswordVisible = confirmPasswordVisible,
        onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
        onConfirmPasswordVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
        onNameChange = onNameChange,
        onEmailChange = onEmailChange,
        onPhoneChange = onPhoneChange,
        onPasswordChange = onPasswordChange,
        onConfirmPasswordChange = onConfirmPasswordChange,
        onAgreeToTermsChange = onAgreeToTermsChange,
        onSignUp = onSignUp,
        onNavigateToSignIn = onNavigateToSignIn
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreenContent(
    state: SignUpState,
    passwordVisible: Boolean = false,
    confirmPasswordVisible: Boolean = false,
    onPasswordVisibilityChange: () -> Unit = {},
    onConfirmPasswordVisibilityChange: () -> Unit = {},
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onAgreeToTermsChange: (Boolean) -> Unit,
    onSignUp: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    val isPasswordTooShort by remember(state.password) {
        derivedStateOf { state.password.isNotEmpty() && state.password.length < 6 }
    }

    val doPasswordsMatch by remember(state.password, state.confirmPassword) {
        derivedStateOf { state.confirmPassword.isEmpty() || state.password == state.confirmPassword }
    }

    val isSignUpEnabled by remember(state.isLoading, state.agreeToTerms) {
        derivedStateOf { !state.isLoading && state.agreeToTerms }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    textAlign = TextAlign.Start


                )

                Text(
                    text = "Sign up to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))


                Text(
                    text = "Full Name",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    textAlign = TextAlign.Start
                )
                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    shape = MaterialTheme.shapes.large,
                    colors = textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF514EB7),
                        unfocusedContainerColor = Color(0x17A9A7A5),
                        focusedContainerColor = Color.Transparent,
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Full Name",
                            tint = Color(0xFF514EB7)
                        )
                    },
                    placeholder = { Text("Enter your full name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !state.isLoading,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "Email",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    textAlign = TextAlign.Start
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    shape = MaterialTheme.shapes.large,
                    colors = textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF514EB7),
                        unfocusedContainerColor = Color(0x17A9A7A5),
                        focusedContainerColor = Color.Transparent,
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = Color(0xFF514EB7)
                        )
                    },
                    placeholder = { Text("Enter your email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    enabled = !state.isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Phone Number",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    textAlign = TextAlign.Start
                )

                OutlinedTextField(
                    value = state.phone,
                    onValueChange = onPhoneChange,
                    shape = MaterialTheme.shapes.large,
                    colors = textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF514EB7),
                        unfocusedContainerColor = Color(0x17A9A7A5),
                        focusedContainerColor = Color.Transparent,
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone Number",
                            tint = Color(0xFF514EB7)
                        )
                    },
                    placeholder = { Text("Enter your phone number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    enabled = !state.isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Password",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    textAlign = TextAlign.Start
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    shape = MaterialTheme.shapes.large,
                    colors = textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF514EB7),
                        unfocusedContainerColor = Color(0x17A9A7A5),
                        focusedContainerColor = Color.Transparent,
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password",
                            tint = Color(0xFF514EB7)
                        )
                    },
                    placeholder = { Text("Enter your password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    trailingIcon = {
                        IconButton(onClick = onPasswordVisibilityChange) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    enabled = !state.isLoading,
                    supportingText = if (isPasswordTooShort) {
                        { Text("Password must be at least 6 characters") }
                    } else null
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Confirm Password",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    textAlign = TextAlign.Start
                )

                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    shape = MaterialTheme.shapes.large,
                    colors = textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF514EB7),
                        unfocusedContainerColor = Color(0x17A9A7A5),
                        focusedContainerColor = Color.Transparent,
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Confirm Password",
                            tint = Color(0xFF514EB7)
                        )
                    },
                    placeholder = { Text("Re-enter your password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (state.agreeToTerms) onSignUp()
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = onConfirmPasswordVisibilityChange) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    enabled = !state.isLoading,
                    isError = !doPasswordsMatch,
                    supportingText = if (!doPasswordsMatch) {
                        { Text("Passwords do not match", color = MaterialTheme.colorScheme.error) }
                    } else null
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(
                        checked = state.agreeToTerms,
                        onCheckedChange = onAgreeToTermsChange,
                        enabled = !state.isLoading,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF514EB7),
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I agree to the Terms and Conditions",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
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
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onSignUp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isSignUpEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF514EB7),
                        contentColor = Color.White
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Sign Up",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(
                        onClick = onNavigateToSignIn,
                        enabled = !state.isLoading
                    ) {
                        Text(
                            "Sign In",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF514EB7)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(
    name = "Sign Up",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SignUpScreenPreview() {
    PixelbitTheme {
        SignUpScreenContent(
            state = SignUpState(
                name = "Ibrahim Mohamed",
                email = "Ibrahim@gamil.com",
                phone = "01102255886",
                password = "123456q",
                confirmPassword = "123456q",
                agreeToTerms = true
            ),
            onNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onAgreeToTermsChange = {},
            onSignUp = {},
            onNavigateToSignIn = {}
        )
    }
}

