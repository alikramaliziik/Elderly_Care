package com.example.elderly_care

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

enum class ActivityType {
    MEDICATION, DOCTOR_VISIT, EXERCISE, MEAL, SOCIAL, THERAPY, OTHER
}

data class DailyActivity(
    val id: String,
    val title: String,
    val time: String,
    val description: String,
    val type: ActivityType,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.NORMAL
)

enum class Priority {
    HIGH, NORMAL, LOW
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel()
) {
    // Collect UI state from ViewModel using lifecycle-aware collection
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.semantics {
                            contentDescription = "Dashboard header showing today's schedule for ${uiState.currentDate}"
                        }
                    ) {
                        Text(
                            "Today's Schedule",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            uiState.currentDate,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier.semantics {
                            contentDescription = "Profile button"
                            role = Role.Button
                        }
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.size(28.dp))
                    }
                    
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.semantics {
                            contentDescription = "Logout button"
                            role = Role.Button
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
                onClick = { /* Add new activity - could open dialog */ },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics {
                    contentDescription = "Add new activity button"
                    role = Role.Button
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Activity", modifier = Modifier.size(28.dp))
            }
        },
        snackbarHost = {
            // Show error message if exists
            if (uiState.hasError) {
                Snackbar(
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(uiState.errorMessage ?: "An error occurred")
                }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Summary Section
                SummarySection(uiState)

                Spacer(modifier = Modifier.height(16.dp))

                // Section Header
                Text(
                    "Daily Activities",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .semantics { heading() }
                )

                // Activities List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics {
                            contentDescription = "List of ${uiState.totalCount} daily activities"
                        },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.activities, key = { it.id }) { activity ->
                        ActivityCard(
                            activity = activity,
                            onToggleComplete = { viewModel.toggleActivityCompletion(activity.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummarySection(uiState: DashboardUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "Summary: ${uiState.completedCount} of ${uiState.totalCount} activities completed, ${uiState.pendingMedicationCount} medications pending"
            },
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "Completed",
            value = "${uiState.completedCount}/${uiState.totalCount}",
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF4CAF50),
            contentDesc = "Completed activities: ${uiState.completedCount} out of ${uiState.totalCount}",
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "Medications",
            value = "${uiState.pendingMedicationCount}",
            icon = Icons.Default.LocalPharmacy,
            color = Color(0xFF2196F3),
            contentDesc = "Pending medications: ${uiState.pendingMedicationCount}",
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
    contentDesc: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.semantics { contentDescription = contentDesc },
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    title,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    value,
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun ActivityCard(
    activity: DailyActivity,
    onToggleComplete: () -> Unit
) {
    val (backgroundColor, iconColor, icon) = getActivityStyle(activity.type)
    
    val activityDesc = buildString {
        append("${activity.title} at ${activity.time}. ")
        append(activity.description)
        if (activity.priority == Priority.HIGH) append(". High priority")
        append(if (activity.isCompleted) ". Completed" else ". Not completed")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = activityDesc },
        colors = CardDefaults.cardColors(
            containerColor = if (activity.isCompleted) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (activity.priority == Priority.HIGH) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(60.dp)
                        .background(Color.Red, RoundedCornerShape(2.dp))
                        .semantics { contentDescription = "High priority indicator" }
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(backgroundColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "${activity.type.name} icon",
                    tint = iconColor,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (activity.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    activity.time,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = iconColor
                )
                Text(
                    activity.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Checkbox(
                checked = activity.isCompleted,
                onCheckedChange = { onToggleComplete() },
                modifier = Modifier
                    .size(48.dp)
                    .semantics {
                        contentDescription = if (activity.isCompleted)
                            "Mark ${activity.title} as incomplete"
                        else "Mark ${activity.title} as complete"
                        role = Role.Checkbox
                    },
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

