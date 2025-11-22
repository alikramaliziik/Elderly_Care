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
import old.people.elderly_care.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onSignUpSuccess: () -> Unit,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Male") }
    var genderExpanded by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    // Error states
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var phoneErrorMessage by remember { mutableStateOf("") }
    var ageError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordMatchError by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Handle errors from ViewModel
    LaunchedEffect(error) {
        if (error != null) {
            nameError = error?.contains("name", ignoreCase = true) == true
            emailError = error?.contains("email", ignoreCase = true) == true
            phoneError = error?.contains("phone", ignoreCase = true) == true
            passwordError = error?.contains("password", ignoreCase = true) == true
        }
    }

    // Function to validate phone number
    fun validatePhoneNumber(phone: String): Boolean {
        val digitsOnly = phone.filter { it.isDigit() }
        return digitsOnly.length == 10 && digitsOnly.all { it.isDigit() }
    }

    // Function to format phone number for display (optional)
    fun formatPhoneNumber(input: String): String {
        val digits = input.filter { it.isDigit() }
        return when {
            digits.length <= 3 -> digits
            digits.length <= 6 -> "${digits.substring(0, 3)} ${digits.substring(3)}"
            digits.length <= 10 -> "${digits.substring(0, 3)} ${digits.substring(3, 6)} ${digits.substring(6)}"
            else -> digits.substring(0, 10) // Limit to 10 digits
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onBackClick,
                    enabled = !isLoading
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 40.sp,
                modifier = Modifier.semantics { heading() }
            )

            Text(
                text = "Join ElderCare Today",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Error Message from ViewModel
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

            // Full Name Field
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    nameError = false
                    viewModel.clearError()
                },
                label = { Text("Full Name", style = MaterialTheme.typography.titleMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Name icon",
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                },
                isError = nameError,
                supportingText = if (nameError) {
                    { Text("Name is required", color = Color(0xFFFF6B6B)) }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp,
                    color = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
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
                    cursorColor = Color.White,
                    errorBorderColor = Color(0xFFFF6B6B),
                    disabledBorderColor = Color.White.copy(alpha = 0.5f),
                    disabledLabelColor = Color.White.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                    { Text("Valid email required", color = Color(0xFFFF6B6B)) }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
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
                    cursorColor = Color.White,
                    errorBorderColor = Color(0xFFFF6B6B),
                    disabledBorderColor = Color.White.copy(alpha = 0.5f),
                    disabledLabelColor = Color.White.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number Field - UPDATED VALIDATION
            OutlinedTextField(
                value = phone,
                onValueChange = { newValue ->
                    // Allow only digits and limit to 10 characters
                    val digitsOnly = newValue.filter { it.isDigit() }
                    if (digitsOnly.length <= 10) {
                        phone = digitsOnly
                        phoneError = false
                        phoneErrorMessage = ""
                        viewModel.clearError()
                        
                        // Auto-format as user types (optional)
                        // phone = formatPhoneNumber(digitsOnly)
                    }
                },
                label = { Text("Phone Number", style = MaterialTheme.typography.titleMedium) },
                placeholder = { 
                    Text(
                        "10 digit number", 
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 16.sp
                    ) 
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone icon",
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                },
                trailingIcon = {
                    if (phone.isNotEmpty()) {
                        val isValid = validatePhoneNumber(phone)
                        Icon(
                            imageVector = if (isValid) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = if (isValid) "Valid phone" else "Invalid phone",
                            tint = if (isValid) Color(0xFF4CAF50) else Color(0xFFF44336),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                isError = phoneError,
                supportingText = {
                    if (phoneError) {
                        Text(phoneErrorMessage, color = Color(0xFFFF6B6B))
                    } else if (phone.isNotEmpty()) {
                        val digitCount = phone.filter { it.isDigit() }.length
                        Text(
                            "$digitCount/10 digits", 
                            color = if (digitCount == 10) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp,
                    color = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
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
                    cursorColor = Color.White,
                    errorBorderColor = Color(0xFFFF6B6B),
                    disabledBorderColor = Color.White.copy(alpha = 0.5f),
                    disabledLabelColor = Color.White.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Age and Gender Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Age Field
                OutlinedTextField(
                    value = age,
                    onValueChange = { newValue ->
                        // Allow empty or valid digits only
                        if (newValue.isEmpty()) {
                            age = ""
                            ageError = false
                            viewModel.clearError()
                        } else if (newValue.all { it.isDigit() }) {
                            val num = newValue.toIntOrNull()
                            if (num != null && num <= 120) {  // Allow typing up to 120
                                age = newValue
                                ageError = false
                                viewModel.clearError()
                            }
                        }
                    },
                    label = { Text("Age", style = MaterialTheme.typography.titleMedium) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Age icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    },
                    isError = ageError,
                    supportingText = if (ageError) {
                        { Text("Age 18-120", color = Color(0xFFFF6B6B)) }
                    } else null,
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp),
                    shape = RoundedCornerShape(16.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 20.sp,
                        color = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
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
                        cursorColor = Color.White,
                        errorBorderColor = Color(0xFFFF6B6B),
                        disabledBorderColor = Color.White.copy(alpha = 0.5f),
                        disabledLabelColor = Color.White.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,     
                        unfocusedTextColor = Color.White    
                    )
                )

                // Gender Dropdown
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { if (!isLoading) genderExpanded = !genderExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedGender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender", style = MaterialTheme.typography.titleMedium) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (selectedGender == "Male") Icons.Default.Man else Icons.Default.Woman,
                                contentDescription = "Gender icon",
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .menuAnchor(),
                        shape = RoundedCornerShape(16.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 20.sp,
                            color = Color.White
                        ),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                            disabledBorderColor = Color.White.copy(alpha = 0.5f),
                            disabledLabelColor = Color.White.copy(alpha = 0.5f),
                            disabledTrailingIconColor = Color.White.copy(alpha = 0.5f)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        listOf("Male", "Female", "Other").forEach { gender ->
                            DropdownMenuItem(
                                text = { Text(gender) },
                                onClick = {
                                    selectedGender = gender
                                    genderExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (gender) {
                                            "Male" -> Icons.Default.Man
                                            "Female" -> Icons.Default.Woman
                                            else -> Icons.Default.Person
                                        },
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                    passwordMatchError = false
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
                        enabled = !isLoading
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
                    { Text("Min 8 characters", color = Color(0xFFFF6B6B)) }
                } else null,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp,
                    color = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
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
                    cursorColor = Color.White,
                    errorBorderColor = Color(0xFFFF6B6B),
                    disabledBorderColor = Color.White.copy(alpha = 0.5f),
                    disabledLabelColor = Color.White.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    passwordMatchError = false
                    viewModel.clearError()
                },
                label = { Text("Confirm Password", style = MaterialTheme.typography.titleMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Confirm password icon",
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                    }
                },
                isError = passwordMatchError,
                supportingText = if (passwordMatchError) {
                    { Text("Passwords don't match", color = Color(0xFFFF6B6B)) }
                } else null,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
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
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = true,
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    cursorColor = Color.White,
                    errorBorderColor = Color(0xFFFF6B6B),
                    disabledBorderColor = Color.White.copy(alpha = 0.5f),
                    disabledLabelColor = Color.White.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up Button
            Button(
                onClick = {
                    // Reset all errors
                    nameError = fullName.isEmpty()
                    emailError = email.isEmpty() || !email.contains("@")
                    
                    // Enhanced phone validation
                    val phoneDigits = phone.filter { it.isDigit() }
                    phoneError = phoneDigits.length != 10
                    phoneErrorMessage = if (phoneDigits.length != 10) "Exactly 10 digits required" else ""
                    
                    ageError = age.isEmpty() || age.toIntOrNull() == null || age.toInt() !in 18..120
                    passwordError = password.length < 8
                    passwordMatchError = password != confirmPassword

                    if (!nameError && !emailError && !phoneError && !ageError && 
                        !passwordError && !passwordMatchError) {
                        viewModel.signup(
                            fullName = fullName,
                            email = email,
                            password = password,
                            phone = phone, // This will be exactly 10 digits
                            age = age.toInt(),
                            gender = selectedGender
                        ) { result ->
                            result.onSuccess {
                                onSignUpSuccess()
                            }
                        }
                    }
                },
                enabled = !isLoading && fullName.isNotBlank() && email.isNotBlank() && 
                          validatePhoneNumber(phone) && age.isNotBlank() &&
                          password.isNotBlank() && password == confirmPassword,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFF667eea),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        "Create Account",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF667eea)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Already have account
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Already have an account? ",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 18.sp
                )
                TextButton(
                    onClick = onLoginClick,
                    enabled = !isLoading
                ) {
                    Text(
                        "Sign In",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
