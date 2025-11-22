// File: app/src/main/java/old/people/elderly_care/DomainModels.kt
package old.people.elderly_care

import old.people.elderly_care.models.DailyActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class ActivityType {
    MEDICATION,
    DOCTOR_VISIT,
    EXERCISE,
    MEAL,
    SOCIAL,
    THERAPY,
    OTHER
}

enum class Priority {
    LOW,
    NORMAL,
    HIGH
}

data class DashboardUiState(
    val activities: List<DailyActivity> = emptyList(),
    val elderlyNames: Map<Int, String> = emptyMap(),
    val elderlyCount: Int = 0,  
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val currentDate: String = LocalDate.now()
        .format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")),
    val isLoading: Boolean = false
)
