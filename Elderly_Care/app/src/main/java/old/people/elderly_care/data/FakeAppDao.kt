package old.people.elderly_care.data

import old.people.elderly_care.*
import old.people.elderly_care.models.DailyActivity

class FakeAppDao : AppDao {

    val caregivers = mutableListOf<Caregiver>()
    val elderly = mutableListOf<Elderly>()
    val activities = mutableListOf<ActivityEntity>()

    // ---------- CAREGIVER ----------
    override suspend fun getAllCaregivers() = caregivers

    override suspend fun getCaregiverByEmail(email: String) =
        caregivers.find { it.email == email }

    override suspend fun checkEmailExists(email: String) =
        caregivers.find { it.email == email }

    override suspend fun getCaregiverById(id: Int) =
        caregivers.find { it.id == id }

    override suspend fun insertCaregiver(caregiver: Caregiver): Long {
        val id = if (caregiver.id == 0) caregivers.size + 1 else caregiver.id
        caregivers.removeAll { it.id == id }
        caregivers.add(caregiver.copy(id = id))
        return id.toLong()
    }

    override suspend fun updateCaregiver(caregiver: Caregiver) {
        caregivers.replaceAll { if (it.id == caregiver.id) caregiver else it }
    }

    // ---------- ELDERLY ----------
    override suspend fun getAllElderly() = elderly

    override suspend fun getElderlyByCaregiver(caregiverId: Int) =
        elderly.filter { it.caregiverId == caregiverId }

    override suspend fun getElderlyById(elderlyId: Int) =
        elderly.find { it.id == elderlyId }

    override suspend fun insertElderly(e: Elderly): Long {
        val id = if (e.id == 0) elderly.size + 1 else e.id
        elderly.removeAll { it.id == id }
        elderly.add(e.copy(id = id))
        return id.toLong()
    }

    override suspend fun deleteElderly(elderlyId: Int) {
        elderly.removeAll { it.id == elderlyId }
    }

    override suspend fun deleteAllElderlyByCaregiver(caregiverId: Int) {
        elderly.removeAll { it.caregiverId == caregiverId }
    }

    // ---------- ACTIVITIES ----------
    override suspend fun getActivitiesForDate(date: String) =
        activities.filter { it.date == date }

    override suspend fun getActivitiesForElderly(elderlyId: Int) =
        activities.filter { it.elderlyId == elderlyId }

    override suspend fun getActivitiesForElderlyOnDate(
        elderlyId: Int,
        date: String
    ) = activities.filter { it.elderlyId == elderlyId && it.date == date }

    override suspend fun insertActivity(activity: ActivityEntity): Long {
        val id = if (activity.id == 0) activities.size + 1 else activity.id
        activities.removeAll { it.id == id }
        activities.add(activity.copy(id = id))
        return id.toLong()
    }

    override suspend fun updateActivity(activity: ActivityEntity) {
        activities.replaceAll { if (it.id == activity.id) activity else it }
    }

    override suspend fun deleteActivity(id: Int) {
        activities.removeIf { it.id == id }
    }

    override suspend fun deleteActivitiesForElderly(elderlyId: Int) {
        activities.removeAll { it.elderlyId == elderlyId }
    }

    override suspend fun getActivityById(activityId: Int) =
        activities.find { it.id == activityId }

    // ---------- CASCADE ----------
    override suspend fun deleteElderlyWithActivities(elderlyId: Int) {
        deleteActivitiesForElderly(elderlyId)
        deleteElderly(elderlyId)
    }

    // ---------- STATS ----------
    override suspend fun getActivityCountForElderly(elderlyId: Int) =
        activities.count { it.elderlyId == elderlyId }

    override suspend fun getCompletedActivityCountForElderly(elderlyId: Int) =
        activities.count { it.elderlyId == elderlyId && it.isCompleted }
}
