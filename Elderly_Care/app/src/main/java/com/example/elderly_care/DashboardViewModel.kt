package com.example.elderly_care

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elderly_care.data.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel for Dashboard Screen
 * Manages UI state and business logic
 * Survives configuration changes (rotation, etc.)
 */
class DashboardViewModel : ViewModel() {
    
    // Private mutable state flow
    private val _uiState = MutableStateFlow(DashboardUiState())
    
    // Public immutable state flow
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadActivities()
    }
    
    /**
     * Load activities from repository
     * In real app: would load from database/API
     */
    private fun loadActivities() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                // Simulate network delay
                kotlinx.coroutines.delay(500)
                
                val activities = ActivityRepository.getDefaultActivities()
                val currentDate = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()).format(Date())
                
                _uiState.update { currentState ->
                    currentState.copy(
                        activities = activities,
                        isLoading = false,
                        currentDate = currentDate,
                        totalCount = activities.size,
                        completedCount = activities.count { it.isCompleted },
                        pendingMedicationCount = activities.count { 
                            it.type == ActivityType.MEDICATION && !it.isCompleted 
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load activities: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Toggle activity completion status
     */
    fun toggleActivityCompletion(activityId: String) {
        _uiState.update { currentState ->
            val updatedActivities = currentState.activities.map { activity ->
                if (activity.id == activityId) {
                    activity.copy(isCompleted = !activity.isCompleted)
                } else {
                    activity
                }
            }
            
            currentState.copy(
                activities = updatedActivities,
                completedCount = updatedActivities.count { it.isCompleted },
                pendingMedicationCount = updatedActivities.count { 
                    it.type == ActivityType.MEDICATION && !it.isCompleted 
                }
            )
        }
    }
    
    /**
     * Add new activity
     */
    fun addActivity(activity: DailyActivity) {
        _uiState.update { currentState ->
            val updatedActivities = currentState.activities + activity
            currentState.copy(
                activities = updatedActivities,
                totalCount = updatedActivities.size
            )
        }
    }
    
    /**
     * Delete activity
     */
    fun deleteActivity(activityId: String) {
        _uiState.update { currentState ->
            val updatedActivities = currentState.activities.filter { it.id != activityId }
            currentState.copy(
                activities = updatedActivities,
                totalCount = updatedActivities.size,
                completedCount = updatedActivities.count { it.isCompleted }
            )
        }
    }
    
    /**
     * Refresh activities
     */
    fun refreshActivities() {
        loadActivities()
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

