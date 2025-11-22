package old.people.elderly_care.ui.profile

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import old.people.elderly_care.data.Caregiver
import old.people.elderly_care.data.Elderly
import old.people.elderly_care.data.ActivityEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    caregiver: Caregiver,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val viewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(context.applicationContext as Application) as T
            }
        }
    )

    var loaded by remember { mutableStateOf(false) }
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(caregiver.id) {
        if (!loaded) {
            viewModel.loadElderly(caregiver.id)
            loaded = true
        }
    }

    // Show error snackbar if there's an error message
    if (errorMessage != null) {
        LaunchedEffect(errorMessage) {
            // You could show a snackbar here instead of just clearing
            // For now, we'll auto-clear after a delay
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }

    val elderlyList by viewModel.elderlyList.collectAsState()
    val selectedElderly by viewModel.selectedElderly.collectAsState()
    val activities by viewModel.selectedElderlyActivities.collectAsState()
    val showDialog by viewModel.showElderlyDialog.collectAsState()
    val showEditDialog by viewModel.showEditElderlyDialog.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()
    val elderlyToEdit by viewModel.elderlyToEdit.collectAsState()
    val elderlyToDelete by viewModel.elderlyToDelete.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6650a4),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddElderlyDialog() },
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Elderly")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFF5F5F5), Color(0xFFE8EAF6))
                    )
                )
                .verticalScroll(rememberScrollState())
        ) {
            // Show error message if any
            if (errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Caregiver Profile Header
            PersonHeaderCard(
                name = caregiver.fullName,
                age = caregiver.age,
                gradient = listOf(Color(0xFF667eea), Color(0xFF764ba2))
            )
            
            // Contact Info Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    icon = Icons.Default.Email,
                    title = "Email",
                    value = caregiver.email,
                    bg = Color(0xFFE3F2FD),
                    iconColor = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    icon = Icons.Default.Phone,
                    title = "Phone",
                    value = caregiver.phone,
                    bg = Color(0xFFE8F5E9),
                    iconColor = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(Modifier.height(16.dp))

            // Elderly List Section
            Text(
                "Elderly Under Care",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6650a4),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))

            if (elderlyList.isEmpty()) {
                EmptyElderlyCard()
            } else {
                elderlyList.forEach { elderly ->
                    ElderlyCard(
                        elderly = elderly,
                        isSelected = selectedElderly?.id == elderly.id,
                        onClick = { viewModel.selectElderly(elderly) },
                        onEdit = { viewModel.showEditElderlyDialog(elderly) },
                        onDelete = { viewModel.showDeleteElderlyDialog(elderly) }
                    )
                }
            }

            // Show activities only if elderly is selected
            selectedElderly?.let { elderly ->
                Spacer(Modifier.height(16.dp))
                TodayActivitiesCard(activities = activities, elderlyName = elderly.fullName)
            }

            Spacer(Modifier.height(32.dp))
            
            // Activity Summary Section
            Text(
                "Weekly Activity Summary",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6650a4),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
            ActivitySummaryCard()
            
            Spacer(Modifier.height(32.dp))
        }
    }

    // Add Elderly Dialog
    if (showDialog) {
        AddElderlyDialog(
            caregiverId = caregiver.id,
            viewModel = viewModel,
            onDismiss = { viewModel.hideAddElderlyDialog() }
        )
    }

    // Edit Elderly Dialog
    if (showEditDialog && elderlyToEdit != null) {
        EditElderlyDialog(
            elderly = elderlyToEdit!!,
            viewModel = viewModel,
            onDismiss = { viewModel.hideEditElderlyDialog() }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && elderlyToDelete != null) {
        DeleteElderlyDialog(
            elderly = elderlyToDelete!!,
            viewModel = viewModel,
            caregiverId = caregiver.id,
            onDismiss = { viewModel.hideDeleteElderlyDialog() }
        )
    }
}

@Composable
private fun PersonHeaderCard(name: String, age: Int, gradient: List<Color>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradient))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                val initials = name.split(" ")
                    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                    .joinToString("")
                Text(
                    text = initials,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = gradient[0]
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = name, 
                fontSize = 28.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color.White
            )
            Text(
                text = "$age years old", 
                fontSize = 18.sp, 
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    bg: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon, 
                    contentDescription = null, 
                    tint = Color.White, 
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = title, 
                fontSize = 12.sp, 
                color = Color.Gray, 
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = iconColor,
                maxLines = 2,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun EmptyElderlyCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Face, 
                contentDescription = null, 
                tint = Color(0xFFFF9800), 
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "No elderly assigned yet", 
                fontWeight = FontWeight.Medium, 
                color = Color.Gray
            )
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun ElderlyCard(
    elderly: Elderly, 
    isSelected: Boolean, 
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF3E0) else Color(0xFFFFF8E1)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF9800)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Face, 
                    contentDescription = null, 
                    tint = Color.White, 
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = elderly.fullName, 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color(0xFFE65100)
                )
                Text(
                    text = "${elderly.age} years • ${elderly.gender}", 
                    fontSize = 14.sp, 
                    color = Color.Gray
                )
                Text(
                    text = elderly.phone ?: "N/A", 
                    fontSize = 14.sp, 
                    color = Color.Gray.copy(alpha = 0.8f)
                )
            }
            
            // Selection indicator and menu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Default.CheckCircle, 
                        contentDescription = "Selected", 
                        tint = Color(0xFF4CAF50)
                    )
                }
                
                // Menu button
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color.Black
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Information") },
                            onClick = {
                                onEdit()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color(0xFF2196F3)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Profile", color = Color.Red) },
                            onClick = {
                                onDelete()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayActivitiesCard(activities: List<ActivityEntity>, elderlyName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Today's Activities for $elderlyName", 
                fontWeight = FontWeight.Bold, 
                color = Color(0xFF6650a4),
                fontSize = 16.sp
            )
            Spacer(Modifier.height(8.dp))
            
            if (activities.isEmpty()) {
                Text(
                    text = "No activities scheduled today.", 
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                activities.forEach { activity ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = "• ${activity.time} - ${activity.title}", 
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        if (activity.isCompleted) {
                            Icon(
                                Icons.Default.Check, 
                                contentDescription = "Done", 
                                tint = Color(0xFF4CAF50), 
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivitySummaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Weekly Activity Completion",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6650a4)
            )
            Spacer(Modifier.height(16.dp))
            
            // Simple activity summary with progress bars
            val weeklyData = listOf(
                "Monday" to 3,
                "Tuesday" to 5,
                "Wednesday" to 6,
                "Thursday" to 4,
                "Friday" to 7,
                "Saturday" to 5,
                "Sunday" to 6
            )
            
            weeklyData.forEach { (day, count) ->
                ActivityProgressRow(day = day, completed = count, total = 8)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ActivityProgressRow(day: String, completed: Int, total: Int) {
    val progress = completed.toFloat() / total.toFloat()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = day,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(80.dp),
            fontSize = 14.sp
        )
        Spacer(Modifier.width(8.dp))
        
        // Progress bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(Color(0xFF4CAF50), RoundedCornerShape(6.dp))
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "$completed/$total",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddElderlyDialog(
    caregiverId: Int,
    viewModel: ProfileViewModel,
    onDismiss: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var phone by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Elderly Person") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                    label = { Text("Age") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = { 
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) 
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded, 
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        listOf("Male", "Female", "Other").forEach { g ->
                            DropdownMenuItem(
                                text = { Text(g) },
                                onClick = {
                                    gender = g
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (fullName.isNotBlank() && age.isNotBlank()) {
                        viewModel.addElderly(caregiverId, fullName, age, gender, phone) { 
                            onDismiss() 
                        }
                    }
                },
                enabled = fullName.isNotBlank() && age.isNotBlank()
            ) { 
                Text("Save") 
            }
        },
        dismissButton = { 
            TextButton(onClick = onDismiss) { 
                Text("Cancel") 
            } 
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditElderlyDialog(
    elderly: Elderly,
    viewModel: ProfileViewModel,
    onDismiss: () -> Unit
) {
    var fullName by remember { mutableStateOf(elderly.fullName) }
    var age by remember { mutableStateOf(elderly.age.toString()) }
    var gender by remember { mutableStateOf(elderly.gender) }
    var phone by remember { mutableStateOf(elderly.phone ?: "") }
    var genderExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Elderly Information") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                    label = { Text("Age") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = { 
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) 
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded, 
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        listOf("Male", "Female", "Other").forEach { g ->
                            DropdownMenuItem(
                                text = { Text(g) },
                                onClick = {
                                    gender = g
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (fullName.isNotBlank() && age.isNotBlank()) {
                        viewModel.updateElderly(elderly.id, fullName, age, gender, phone) { 
                            onDismiss() 
                        }
                    }
                },
                enabled = fullName.isNotBlank() && age.isNotBlank()
            ) { 
                Text("Update") 
            }
        },
        dismissButton = { 
            TextButton(onClick = onDismiss) { 
                Text("Cancel") 
            } 
        }
    )
}

@Composable
private fun DeleteElderlyDialog(
    elderly: Elderly,
    viewModel: ProfileViewModel,
    caregiverId: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Elderly Profile") },
        text = { 
            Text("Are you sure you want to delete ${elderly.fullName}'s profile? This action cannot be undone and will also remove all associated activities.") 
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.deleteElderly(caregiverId)
                }
            ) {
                Text("Delete", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
