package old.people.elderly_care

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import old.people.elderly_care.data.AppDao
import old.people.elderly_care.data.UserPreferencesContract
import old.people.elderly_care.data.toDomain
import old.people.elderly_care.data.toEntity
import old.people.elderly_care.models.DailyActivity
import java.time.LocalDate
import java.util.Calendar

class DashboardViewModel(
    private val dao: AppDao,
    private val prefs: UserPreferencesContract,   // <-- FIXED (interface instead of final class)
    private val caregiverId: Int,
    private val context: Context? = null          // context still optional for tests
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadTodayActivities()
    }

    fun refreshActivities() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(800)
            loadTodayActivities()
            prefs.setLastSync(System.currentTimeMillis())
        }
    }

    private fun loadTodayActivities() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()

            // 1. Elderly for this caregiver
            val myElderly = dao.getElderlyByCaregiver(caregiverId)
            val elderlyIds = myElderly.map { it.id }

            // 2. All activities for today
            val allToday = dao.getActivitiesForDate(today)

            // 3. Filter by this caregiver's elderly
            val myActivities = allToday
                .filter { it.elderlyId in elderlyIds }
                .map { it.toDomain() }

            // 4. Elderly names map
            val names = myElderly.associate { it.id to it.fullName }

            _uiState.update {
                it.copy(
                    activities = myActivities,
                    elderlyNames = names,
                    elderlyCount = myElderly.size,
                    completedCount = myActivities.count { a -> a.isCompleted },
                    totalCount = myActivities.size,
                    isLoading = false,
                    currentDate = formatDate(today)
                )
            }
        }
    }

    fun toggleActivityCompletion(id: Int) {
        viewModelScope.launch {
            val activity = _uiState.value.activities.find { it.id == id } ?: return@launch
            val updated = activity.copy(isCompleted = !activity.isCompleted)

            dao.insertActivity(updated.toEntity())

            if (updated.isCompleted) {
                cancelNotification(id)
            } else {
                val elderlyName = _uiState.value.elderlyNames[activity.elderlyId] ?: "Elderly"
                scheduleNotification(updated, elderlyName)
            }

            loadTodayActivities()
        }
    }

    fun addActivity(activity: DailyActivity) {
        viewModelScope.launch {
            dao.insertActivity(activity.toEntity())

            val elderlyName = _uiState.value.elderlyNames[activity.elderlyId] ?: "Elderly"
            scheduleNotification(activity, elderlyName)

            loadTodayActivities()
        }
    }

    fun deleteActivity(id: Int) {
        viewModelScope.launch {
            cancelNotification(id)
            dao.deleteActivity(id)
            loadTodayActivities()
        }
    }

    // ------------------ NOTIFICATION SYSTEM -----------------------

    private fun scheduleNotification(activity: DailyActivity, elderlyName: String) {
        val context = context ?: return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ActivityNotificationReceiver::class.java).apply {
            putExtra("activity_title", activity.title)
            putExtra("elderly_name", elderlyName)
            putExtra("activity_time", activity.time)
            putExtra("activity_id", activity.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            activity.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Parse "08:00" or "02:30 PM"
        val timeParts = activity.time.split(":")
        var hour = timeParts[0].toIntOrNull() ?: 0
        val minute = timeParts.getOrNull(1)?.split(" ")?.first()?.toIntOrNull() ?: 0

        if (activity.time.contains("PM", ignoreCase = true) && hour < 12) hour += 12
        if (activity.time.contains("AM", ignoreCase = true) && hour == 12) hour = 0

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun cancelNotification(activityId: Int) {
        val context = context ?: return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ActivityNotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            activityId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun formatDate(dateString: String): String {
        return try {
            val date = LocalDate.parse(dateString)
            date.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
        } catch (e: Exception) {
            "Today"
        }
    }
}
