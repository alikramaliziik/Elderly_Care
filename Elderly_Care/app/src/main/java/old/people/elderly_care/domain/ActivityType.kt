// domain/Activity.kt
package old.people.elderly_care.domain

enum class ActivityType { MEAL, MEDICATION, EXERCISE, HYGIENE, APPOINTMENT, OTHER }

enum class Priority { LOW, MEDIUM, HIGH }

data class DailyActivity(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val type: ActivityType,
    val priority: Priority,
    val time: String? = null,          // "08:00-09:00"
    val isCompleted: Boolean = false
)
