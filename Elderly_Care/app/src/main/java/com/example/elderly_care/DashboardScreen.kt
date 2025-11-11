package old.people.elderly_care

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import old.people.elderly_care.data.AppDatabase
import old.people.elderly_care.data.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    onLogout: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // ViewModel with Room + DataStore
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<DashboardViewModel>(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(
                    dao = AppDatabase.getInstance(context).dao(),
                    prefs = UserPreferences(context)
                ) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Group activities by time period
    val morningActivities = uiState.activities.filter { time ->
        val hour = time.time.split(":")[0].toIntOrNull() ?: 0
        hour < 12
    }
    val afternoonActivities = uiState.activities.filter { time ->
        val hour = time.time.split(":")[0].toIntOrNull() ?: 0
        hour in 12..16
    }
    val eveningActivities = uiState.activities.filter { time ->
        val hour = time.time.split(":")[0].toIntOrNull() ?: 0
        hour > 16
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.semantics {
                            contentDescription = "Dashboard header - ${uiState.currentDate}"
                        }
                    ) {
                        Text(
                            "Daily Schedule",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            uiState.currentDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate("profile") },
                        modifier = Modifier.semantics {
                            contentDescription = "Profile button"
                        }
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.size(28.dp))
                    }
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.semantics {
                            contentDescription = "Logout button"
                        }
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_activity") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Activity", modifier = Modifier.size(28.dp))
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refreshActivities() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Summary
                item {
                    SummarySection(uiState)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Morning Section
                if (morningActivities.isNotEmpty()) {
                    item {
                        TimeSection(
                            title = "Morning",
                            startTime = "06:00 AM",
                            endTime = "12:00 PM",
                            icon = Icons.Default.Brightness7,
                            color = Color(0xFFFF9800)
                        )
                    }
                    items(morningActivities.sortedBy { it.time }) { activity ->
                        TimeBasedActivityCard(
                            activity = activity,
                            onToggleComplete = { viewModel.toggleActivityCompletion(activity.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // Afternoon Section
                if (afternoonActivities.isNotEmpty()) {
                    item {
                        TimeSection(
                            title = "Afternoon",
                            startTime = "12:00 PM",
                            endTime = "06:00 PM",
                            icon = Icons.Default.WbSunny,
                            color = Color(0xFFFF5722)
                        )
                    }
                    items(afternoonActivities.sortedBy { it.time }) { activity ->
                        TimeBasedActivityCard(
                            activity = activity,
                            onToggleComplete = { viewModel.toggleActivityCompletion(activity.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // Evening Section
                if (eveningActivities.isNotEmpty()) {
                    item {
                        TimeSection(
                            title = "Evening",
                            startTime = "06:00 PM",
                            endTime = "10:00 PM",
                            icon = Icons.Default.Brightness4,
                            color = Color(0xFF673AB7)
                        )
                    }
                    items(eveningActivities.sortedBy { it.time }) { activity ->
                        TimeBasedActivityCard(
                            activity = activity,
                            onToggleComplete = { viewModel.toggleActivityCompletion(activity.id) }
                        )
                    }
                }
            }
        }
    }
}

// ————————————————————————————————————————
// ALL YOUR ORIGINAL UI COMPONENTS BELOW
// ————————————————————————————————————————

@Composable
fun TimeSection(
    title: String,
    startTime: String,
    endTime: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(2.dp, color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    "$startTime - $endTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun SummarySection(uiState: DashboardUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "Completed",
            value = "${uiState.completedCount}/${uiState.totalCount}",
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "Urgent",
            value = "${uiState.activities.count { it.priority == Priority.HIGH }}",
            icon = Icons.Default.Warning,
            color = Color(0xFFFF5252),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TimeBasedActivityCard(
    activity: DailyActivity,
    onToggleComplete: () -> Unit
) {
    val (backgroundColor, iconColor, icon) = getActivityStyle(activity.type)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (activity.isCompleted) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Time Display
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .background(backgroundColor, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    activity.time,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = iconColor,
                    textAlign = TextAlign.Center
                )
            }

            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(backgroundColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "${activity.type.name} icon",
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        activity.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (activity.isCompleted)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface
                    )

                    // Priority Stars
                    if (activity.priority == Priority.HIGH) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "High priority",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "High priority",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                    } else if (activity.priority == Priority.NORMAL) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Normal priority",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    activity.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }

            // Checkbox
            Checkbox(
                checked = activity.isCompleted,
                onCheckedChange = { onToggleComplete() },
                modifier = Modifier.size(44.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4CAF50),
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

@Composable
fun getActivityStyle(type: ActivityType): Triple<Color, Color, ImageVector> {
    return when (type) {
        ActivityType.MEDICATION -> Triple(Color(0xFFE3F2FD), Color(0xFF2196F3), Icons.Default.LocalPharmacy)
        ActivityType.DOCTOR_VISIT -> Triple(Color(0xFFFCE4EC), Color(0xFFE91E63), Icons.Default.LocalHospital)
        ActivityType.EXERCISE -> Triple(Color(0xFFE8F5E9), Color(0xFF4CAF50), Icons.Default.FitnessCenter)
        ActivityType.MEAL -> Triple(Color(0xFFFFF3E0), Color(0xFFFF9800), Icons.Default.Restaurant)
        ActivityType.SOCIAL -> Triple(Color(0xFFF3E5F5), Color(0xFF9C27B0), Icons.Default.People)
        ActivityType.THERAPY -> Triple(Color(0xFFE0F2F1), Color(0xFF009688), Icons.Default.Favorite)
        ActivityType.OTHER -> Triple(Color(0xFFF5F5F5), Color(0xFF757575), Icons.Default.EventNote)
    }
}
