package old.people.elderly_care.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import old.people.elderly_care.data.Caregiver
import old.people.elderly_care.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (Caregiver) -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Show error message
    LaunchedEffect(error) {
        if (error != null) {
            emailError = error?.contains("email", ignoreCase = true) == true
            passwordError = error?.contains("password", ignoreCase = true) == true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
            .semantics {
                contentDescription = "Login screen for ElderCare application"
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .semantics {
                        contentDescription = "ElderCare application logo"
                    },
                shape = RoundedCornerShape(50.dp),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize(),
                    tint = Color(0xFF667eea)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Title
            Text(
                text = "ElderCare",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 48.sp,
                modifier = Modifier.semantics { heading() }
            )

            Text(
                text = "Caring Together",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White.copy(alpha = 0.95f),
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Error Message
            if (error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6B6B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text(error ?: "", color = Color.White, fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                    viewModel.clearError()
                },
                label = { Text("Email", style = MaterialTheme.typography.titleMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email icon",
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                },
                isError = emailError,
                supportingText = if (emailError) {
                    { Text("Please enter a valid email", color = Color(0xFFFF6B6B)) }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .semantics {
                        contentDescription = "Email input field"
                        if (emailError) error("Please enter a valid email")
                    },
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp,
                    color = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    errorBorderColor = Color(0xFFFF6B6B),
                    cursorColor = Color.White,
                    disabledBorderColor = Color.White.copy(alpha = 0.5f),
                    disabledLabelColor = Color.White.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                    viewModel.clearError()
                },
                label = { Text("Password", style = MaterialTheme.typography.titleMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password icon",
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        enabled = !isLoading,
                        modifier = Modifier.semantics {
                            contentDescription = if (passwordVisible)
                                "Hide password"
                            else
                                "Show password"
                            role = Role.Button
                        }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                    }
                },
                isError = passwordError,
                supportingText = if (passwordError) {
                    { Text("Password is required", color = Color(0xFFFF6B6B)) }
                } else null,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .semantics {
                        contentDescription = "Password input field"
                        if (!passwordVisible) password()
                        if (passwordError) error("Password is required")
                    },
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp,
                    color = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            viewModel.login(email, password) { result ->
                                result.onSuccess { caregiver ->
                                    onLoginSuccess(caregiver)
                                }
                            }
                        } else {
                            emailError = email.isEmpty()
                            passwordError = password.isEmpty()
                        }
                    }
                ),
                singleLine = true,
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    errorBorderColor = Color(0xFFFF6B6B),
                    cursorColor = Color.White,
                    disabledBorderColor = Color.White.copy(alpha = 0.5f),
                    disabledLabelColor = Color.White.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onForgotPasswordClick,
                    enabled = !isLoading,
                    modifier = Modifier.semantics {
                        contentDescription = "Forgot password link"
                        role = Role.Button
                    }
                ) {
                    Text(
                        "Forgot Password?",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Sign In Button
            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        emailError = email.isEmpty()
                        passwordError = password.isEmpty()
                    } else {
                        viewModel.login(email, password) { result ->
                            result.onSuccess { caregiver ->
                                onLoginSuccess(caregiver)
                            }
                        }
                    }
                },
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .semantics {
                        contentDescription = "Sign in button"
                        role = Role.Button
                    },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFF667eea),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        "Sign In",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF667eea)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.5f), thickness = 1.dp)
                Text(
                    "  or  ",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp
                )
                Divider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.5f), thickness = 1.dp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sign Up
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Don't have an account? ",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp
                )
                TextButton(
                    onClick = onSignUpClick,
                    enabled = !isLoading,
                    modifier = Modifier.semantics {
                        contentDescription = "Create account button"
                        role = Role.Button
                    }
                ) {
                    Text(
                        "Sign Up",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
