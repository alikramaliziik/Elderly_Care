package old.people.elderly_care.data

import androidx.room.*

@Dao
interface AppDao {

    // -------- CAREGIVERS --------

    @Query("SELECT * FROM caregivers")
    suspend fun getAllCaregivers(): List<Caregiver>

    @Query("SELECT * FROM caregivers WHERE email = :email LIMIT 1")
    suspend fun getCaregiverByEmail(email: String): Caregiver?

    @Query("SELECT * FROM caregivers WHERE email = :email LIMIT 1")
    suspend fun checkEmailExists(email: String): Caregiver?

    @Query("SELECT * FROM caregivers WHERE id = :id LIMIT 1")
    suspend fun getCaregiverById(id: Int): Caregiver?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaregiver(caregiver: Caregiver): Long

    @Update
    suspend fun updateCaregiver(caregiver: Caregiver)



    // -------- ELDERLY --------

    @Query("SELECT * FROM elderly")
    suspend fun getAllElderly(): List<Elderly>

    @Query("SELECT * FROM elderly WHERE caregiver_id = :caregiverId")
    suspend fun getElderlyByCaregiver(caregiverId: Int): List<Elderly>

    @Query("SELECT * FROM elderly WHERE id = :elderlyId")
    suspend fun getElderlyById(elderlyId: Int): Elderly?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElderly(elderly: Elderly): Long

    @Query("DELETE FROM elderly WHERE id = :elderlyId")
    suspend fun deleteElderly(elderlyId: Int)

    @Query("DELETE FROM elderly WHERE caregiver_id = :caregiverId")
    suspend fun deleteAllElderlyByCaregiver(caregiverId: Int)



    // -------- ACTIVITIES --------

    @Query("SELECT * FROM activities WHERE date = :date ORDER BY time")
    suspend fun getActivitiesForDate(date: String): List<ActivityEntity>

    @Query("SELECT * FROM activities WHERE elderly_id = :elderlyId ORDER BY date, time")
    suspend fun getActivitiesForElderly(elderlyId: Int): List<ActivityEntity>

    @Query("SELECT * FROM activities WHERE elderly_id = :elderlyId AND date = :date ORDER BY time")
    suspend fun getActivitiesForElderlyOnDate(elderlyId: Int, date: String): List<ActivityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity): Long

    @Update
    suspend fun updateActivity(activity: ActivityEntity)

    @Query("DELETE FROM activities WHERE id = :id")
    suspend fun deleteActivity(id: Int)

    @Query("DELETE FROM activities WHERE elderly_id = :elderlyId")
    suspend fun deleteActivitiesForElderly(elderlyId: Int)

    @Query("SELECT * FROM activities WHERE id = :activityId")
    suspend fun getActivityById(activityId: Int): ActivityEntity?



    // -------- CASCADE DELETE --------

    @Transaction
    suspend fun deleteElderlyWithActivities(elderlyId: Int) {
        deleteActivitiesForElderly(elderlyId)
        deleteElderly(elderlyId)
    }



    // -------- STATS --------

    @Query("SELECT COUNT(*) FROM activities WHERE elderly_id = :elderlyId")
    suspend fun getActivityCountForElderly(elderlyId: Int): Int

    @Query("SELECT COUNT(*) FROM activities WHERE elderly_id = :elderlyId AND is_completed = 1")
    suspend fun getCompletedActivityCountForElderly(elderlyId: Int): Int
}
