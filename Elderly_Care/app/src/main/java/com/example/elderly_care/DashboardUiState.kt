package com.example.elderly_care

/**
 * UI State for Dashboard Screen
 * Represents all data needed to render the UI
 */
data class DashboardUiState(
    val activities: List<DailyActivity> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val currentDate: String = "",
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val pendingMedicationCount: Int = 0
) {
    // Derived properties for statistics
    val completionPercentage: Float
        get() = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    
    val hasError: Boolean
        get() = errorMessage != null
}

