// File: app/src/main/java/old/people/elderly_care/ui/add/AddActivityScreen.kt
package old.people.elderly_care.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import old.people.elderly_care.ActivityType
import old.people.elderly_care.DashboardViewModel
import old.people.elderly_care.Priority
import old.people.elderly_care.data.AppDatabase
import old.people.elderly_care.data.Elderly
import old.people.elderly_care.data.UserPreferences
import old.people.elderly_care.models.DailyActivity
import old.people.elderly_care.ui.profile.ProfileViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(
    navController: NavController,
    caregiverId: Int
) {
    val context = LocalContext.current

    // ProfileViewModel for Elderly List
    val profileViewModel: ProfileViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(context.applicationContext as android.app.Application) as T
            }
        }
    )

    // DashboardViewModel for Adding Activity
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(
                    dao = AppDatabase.getInstance(context).dao(),
                    prefs = UserPreferences(context),
                    caregiverId = caregiverId,
                    context = context
                ) as T
            }
        }
    )

    // Load elderly list for this caregiver - REFRESH EVERY TIME SCREEN IS OPENED
    LaunchedEffect(caregiverId) {
        profileViewModel.loadElderly(caregiverId)
    }

    val elderlyList by profileViewModel.elderlyList.collectAsState()

    // If no elderly → show block screen
    if (elderlyList.isEmpty()) {
        NoElderlyScreen(navController = navController, caregiverId = caregiverId)
        return
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ActivityType.MEDICATION) }
    var selectedPriority by remember { mutableStateOf(Priority.NORMAL) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedElderly by remember { mutableStateOf<Elderly?>(null) }

    var typeExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }
    var elderlyExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    val timePickerState = rememberTimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute,
        is24Hour = false
    )

    // Reset selected elderly if it's no longer in the list (was deleted)
    LaunchedEffect(elderlyList) {
        if (selectedElderly != null && elderlyList.none { it.id == selectedElderly!!.id }) {
            selectedElderly = null
        }
        
        // Auto-select first elderly if none selected and list is not empty
        if (selectedElderly == null && elderlyList.isNotEmpty()) {
            selectedElderly = elderlyList.first()
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = java.time.Instant
                            .ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    selectedTime = String.format("%02d:%02d", hour, minute)
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Activity") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Elderly Count Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${elderlyList.size} elderly person${if (elderlyList.size != 1) "s" else ""} under care",
                        color = Color(0xFF1976D2),
                        fontSize = 14.sp
                    )
                }
            }

            // === ELDERLY DROPDOWN ===
            ExposedDropdownMenuBox(
                expanded = elderlyExpanded,
                onExpandedChange = { elderlyExpanded = !elderlyExpanded }
            ) {
                OutlinedTextField(
                    value = selectedElderly?.fullName ?: "Select Elderly Person",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Elderly Person") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = elderlyExpanded)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Face, contentDescription = null)
                    },
                    isError = selectedElderly == null
                )
                ExposedDropdownMenu(
                    expanded = elderlyExpanded,
                    onDismissRequest = { elderlyExpanded = false }
                ) {
                    if (elderlyList.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No elderly available") },
                            onClick = { elderlyExpanded = false },
                            enabled = false
                        )
                    } else {
                        elderlyList.forEach { elderly ->
                            DropdownMenuItem(
                                text = { Text(elderly.fullName) },
                                onClick = {
                                    selectedElderly = elderly
                                    elderlyExpanded = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, null)
                                }
                            )
                        }
                    }
                }
            }

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Activity Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Title, contentDescription = null)
                }
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null)
                }
            )

            // Date Selection
            OutlinedTextField(
                value = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                onValueChange = { },
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, "Select date")
                    }
                },
                leadingIcon = {
                    Icon(Icons.Default.Event, contentDescription = null)
                }
            )

            // Time Selection
            OutlinedTextField(
                value = selectedTime.ifEmpty { "Select time" },
                onValueChange = { },
                label = { Text("Time") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(Icons.Default.AccessTime, "Select time")
                    }
                },
                leadingIcon = {
                    Icon(Icons.Default.Schedule, contentDescription = null)
                }
            )

            // Activity Type Dropdown
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded }
            ) {
                OutlinedTextField(
                    value = selectedType.name.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Activity Type") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Category, contentDescription = null)
                    }
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    ActivityType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name.replace("_", " ")) },
                            onClick = {
                                selectedType = type
                                typeExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = getActivityIcon(type),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }

            // Priority Dropdown
            ExposedDropdownMenuBox(
                expanded = priorityExpanded,
                onExpandedChange = { priorityExpanded = !priorityExpanded }
            ) {
                OutlinedTextField(
                    value = when(selectedPriority) {
                        Priority.HIGH -> "High Priority"
                        Priority.NORMAL -> "Normal Priority"
                        Priority.LOW -> "Low Priority"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = when(selectedPriority) {
                                Priority.HIGH -> Icons.Default.PriorityHigh
                                Priority.NORMAL -> Icons.Default.Remove
                                Priority.LOW -> Icons.Default.LowPriority
                            },
                            contentDescription = null
                        )
                    }
                )
                ExposedDropdownMenu(
                    expanded = priorityExpanded,
                    onDismissRequest = { priorityExpanded = false }
                ) {
                    Priority.values().forEach { priority ->
                        DropdownMenuItem(
                            text = {
                                Text(when(priority) {
                                    Priority.HIGH -> "High Priority"
                                    Priority.NORMAL -> "Normal Priority"
                                    Priority.LOW -> "Low Priority"
                                })
                            },
                            onClick = {
                                selectedPriority = priority
                                priorityExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when(priority) {
                                        Priority.HIGH -> Icons.Default.PriorityHigh
                                        Priority.NORMAL -> Icons.Default.Remove
                                        Priority.LOW -> Icons.Default.LowPriority
                                    },
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    selectedElderly?.let { elderly ->
                        if (title.isNotBlank() && selectedTime.isNotEmpty()) {
                            val newActivity = DailyActivity(
                                id = 0,
                                elderlyId = elderly.id,
                                title = title,
                                time = selectedTime,
                                description = description,
                                type = selectedType,
                                isCompleted = false,
                                priority = selectedPriority,
                                date = selectedDate.toString(),
                                caregiverId = caregiverId
                            )
                            dashboardViewModel.addActivity(newActivity)
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && selectedTime.isNotEmpty() && selectedElderly != null
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Activity")
            }

            // Cancel Button
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Cancel, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cancel")
            }
        }
    }
}

// === UPDATED NO ELDERLY SCREEN ===
@Composable
fun NoElderlyScreen(navController: NavController, caregiverId: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.PersonOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "No Elderly Assigned",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            "Please add an elderly person from your profile first.",
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { 
                navController.navigate("profile") 
            }
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Go to Profile")
        }
    }
}

@Composable
fun getActivityIcon(type: ActivityType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        ActivityType.MEDICATION -> Icons.Default.LocalPharmacy
        ActivityType.DOCTOR_VISIT -> Icons.Default.LocalHospital
        ActivityType.EXERCISE -> Icons.Default.FitnessCenter
        ActivityType.MEAL -> Icons.Default.Restaurant
        ActivityType.SOCIAL -> Icons.Default.People
        ActivityType.THERAPY -> Icons.Default.Favorite
        ActivityType.OTHER -> Icons.Default.EventNote
    }
}
