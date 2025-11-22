// File: models/DailyActivity.kt
package old.people.elderly_care.models

import old.people.elderly_care.ActivityType
import old.people.elderly_care.Priority

data class DailyActivity(
    val id: Int = 0,
    val elderlyId: Int,
    val title: String,
    val time: String,
    val description: String,
    val type: ActivityType,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.NORMAL,
    val date: String,
    val caregiverId: Int = 0
)
