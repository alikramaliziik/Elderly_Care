package old.people.elderly_care.ui.aboutme

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import old.people.elderly_care.data.Caregiver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutMeScreen(
    caregiver: Caregiver,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: AboutMeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AboutMeViewModel(context.applicationContext as Application) as T
            }
        }
    )

    // Initialize viewmodel with caregiver data
    LaunchedEffect(caregiver) {
        viewModel.setCaregiver(caregiver)
    }

    val currentCaregiver by viewModel.caregiver.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    val profileImageBitmap by viewModel.profileImageBitmap.collectAsState()
    val showImageSourceDialog by viewModel.showImageSourceDialog.collectAsState()

    // Form fields - initialized from current caregiver state
    var fullName by remember { mutableStateOf(currentCaregiver?.fullName ?: "") }
    var email by remember { mutableStateOf(currentCaregiver?.email ?: "") }
    var phone by remember { mutableStateOf(currentCaregiver?.phone ?: "") }
    var age by remember { mutableStateOf(currentCaregiver?.age?.toString() ?: "") }
    var selectedGender by remember { mutableStateOf(currentCaregiver?.gender ?: "Male") }
    var genderExpanded by remember { mutableStateOf(false) }

    // Error states
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var ageError by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Update form fields when caregiver data changes
    LaunchedEffect(currentCaregiver) {
        currentCaregiver?.let {
            fullName = it.fullName
            email = it.email
            phone = it.phone
            age = it.age.toString()
            selectedGender = it.gender
        }
    }

    // Image picker launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.setProfileImageUri(it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            viewModel.setProfileImageBitmap(it)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Profile",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF667eea)
                )
            )
        }
    ) { padding ->
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
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Error Message
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6B6B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage!!,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Profile Picture Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Image
                        Box(
                            modifier = Modifier.size(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Show selected image, existing profile picture, or placeholder
                            when {
                                profileImageBitmap != null -> {
                                    Image(
                                        bitmap = profileImageBitmap!!.asImageBitmap(),
                                        contentDescription = "Profile picture",
                                        modifier = Modifier
                                            .size(150.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                profileImageUri != null -> {
                                    AsyncImage(
                                        model = profileImageUri,
                                        contentDescription = "Profile picture",
                                        modifier = Modifier
                                            .size(150.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                currentCaregiver?.profilePicture != null -> {
                                    // In real implementation, you'd load the bitmap from bytes
                                    // For now, show placeholder with initials
                                    ProfilePlaceholder(
                                        name = fullName,
                                        size = 150.dp
                                    )
                                }
                                else -> {
                                    ProfilePlaceholder(
                                        name = fullName,
                                        size = 150.dp
                                    )
                                }
                            }

                            // Change Photo Button
                            FloatingActionButton(
                                onClick = { viewModel.showImageSourceDialog() },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(48.dp),
                                containerColor = Color(0xFF667eea),
                                elevation = FloatingActionButtonDefaults.elevation(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = "Change photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Profile Picture",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = "Tap camera icon to change your photo",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Remove Photo Button
                        if (profileImageBitmap != null || profileImageUri != null || currentCaregiver?.profilePicture != null) {
                            TextButton(
                                onClick = { viewModel.clearProfileImage() }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Remove Photo",
                                    color = Color.Red
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Personal Information Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Personal Information",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Full Name Field
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                nameError = false
                                viewModel.clearError()
                            },
                            label = { Text("Full Name", color = Color.Black) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF667eea))
                            },
                            isError = nameError,
                            supportingText = if (nameError) {
                                { Text("Name is required", color = Color.Red) }
                            } else null,
                            modifier = Modifier.fillMaxWidth(),
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
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedLabelColor = Color(0xFF667eea),
                                unfocusedLabelColor = Color.Gray,
                                focusedBorderColor = Color(0xFF667eea),
                                unfocusedBorderColor = Color.Gray,
                                cursorColor = Color.Black
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
                            label = { Text("Email", color = Color.Black) },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF667eea))
                            },
                            isError = emailError,
                            supportingText = if (emailError) {
                                { Text("Valid email required", color = Color.Red) }
                            } else null,
                            modifier = Modifier.fillMaxWidth(),
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
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedLabelColor = Color(0xFF667eea),
                                unfocusedLabelColor = Color.Gray,
                                focusedBorderColor = Color(0xFF667eea),
                                unfocusedBorderColor = Color.Gray,
                                cursorColor = Color.Black
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Phone Field
                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                // Allow only digits and limit to 10 characters
                                val digitsOnly = it.filter { char -> char.isDigit() }
                                if (digitsOnly.length <= 10) {
                                    phone = digitsOnly
                                    phoneError = false
                                    viewModel.clearError()
                                }
                            },
                            label = { Text("Phone Number", color = Color.Black) },
                            placeholder = { Text("10 digit number", color = Color.Gray) },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF667eea))
                            },
                            trailingIcon = {
                                if (phone.isNotEmpty()) {
                                    val isValid = phone.length == 10
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
                                    Text("Exactly 10 digits required", color = Color.Red)
                                } else if (phone.isNotEmpty()) {
                                    Text(
                                        "${phone.length}/10 digits", 
                                        color = if (phone.length == 10) Color(0xFF4CAF50) else Color.Gray
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
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
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedLabelColor = Color(0xFF667eea),
                                unfocusedLabelColor = Color.Gray,
                                focusedBorderColor = Color(0xFF667eea),
                                unfocusedBorderColor = Color.Gray,
                                cursorColor = Color.Black
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
                                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                        val num = newValue.toIntOrNull()
                                        if (num == null || num in 18..120) {
                                            age = newValue
                                            ageError = false
                                            viewModel.clearError()
                                        }
                                    }
                                },
                                label = { Text("Age", color = Color.Black) },
                                leadingIcon = {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF667eea))
                                },
                                isError = ageError,
                                supportingText = if (ageError) {
                                    { Text("Age 18-120", color = Color.Red) }
                                } else null,
                                modifier = Modifier.weight(1f),
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
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedLabelColor = Color(0xFF667eea),
                                    unfocusedLabelColor = Color.Gray,
                                    focusedBorderColor = Color(0xFF667eea),
                                    unfocusedBorderColor = Color.Gray,
                                    cursorColor = Color.Black
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
                                    label = { Text("Gender", color = Color.Black) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (selectedGender == "Male") Icons.Default.Man else Icons.Default.Woman,
                                            contentDescription = null,
                                            tint = Color(0xFF667eea)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    enabled = !isLoading,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedLabelColor = Color(0xFF667eea),
                                        unfocusedLabelColor = Color.Gray,
                                        focusedBorderColor = Color(0xFF667eea),
                                        unfocusedBorderColor = Color.Gray
                                    )
                                )

                                ExposedDropdownMenu(
                                    expanded = genderExpanded,
                                    onDismissRequest = { genderExpanded = false }
                                ) {
                                    listOf("Male", "Female", "Other").forEach { gender ->
                                        DropdownMenuItem(
                                            text = { Text(gender, color = Color.Black) },
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
                                                    contentDescription = null,
                                                    tint = Color(0xFF667eea)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button
                Button(
                    onClick = {
                        // Validate form
                        nameError = fullName.isEmpty()
                        emailError = email.isEmpty() || !email.contains("@")
                        phoneError = phone.length != 10
                        ageError = age.isEmpty() || age.toIntOrNull() == null || age.toInt() !in 18..120

                        if (!nameError && !emailError && !phoneError && !ageError) {
                            viewModel.updateCaregiverProfile(
                                caregiverId = caregiver.id,
                                fullName = fullName,
                                email = email,
                                phone = phone,
                                age = age.toInt(),
                                gender = selectedGender,
                                onSuccess = onSaveSuccess
                            )
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
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
                            "Save Changes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667eea)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Image Source Dialog
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideImageSourceDialog() },
            title = { Text("Choose Photo Source", color = Color.Black) },
            text = { Text("Select how you want to add your profile picture", color = Color.Black) },
            confirmButton = {
                TextButton(
                    onClick = {
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        viewModel.hideImageSourceDialog()
                    }
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFF667eea))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Choose from Gallery", color = Color(0xFF667eea))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        cameraLauncher.launch(null)
                        viewModel.hideImageSourceDialog()
                    }
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFF667eea))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Take Photo", color = Color(0xFF667eea))
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun ProfilePlaceholder(name: String, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF667eea)),
        contentAlignment = Alignment.Center
    ) {
        val initials = name.split(" ")
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
        Text(
            text = if (initials.isNotEmpty()) initials else "?",
            fontSize = (size.value / 3).sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
