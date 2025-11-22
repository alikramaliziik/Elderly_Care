package old.people.elderly_care.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Caregiver::class, Elderly::class, ActivityEntity::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dao(): AppDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "elderly_care.db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateDatabase(database.dao())
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }

        // ONLY for in-memory test database
        suspend fun populateForTest(dao: AppDao) {
            populateDatabase(dao)
        }

        // PRIVATE: real population logic
        private suspend fun populateDatabase(dao: AppDao) {

            // CAREGIVERS
            val caregiver1 = Caregiver(
                fullName = "Sarah Johnson",
                email = "sarah@eldercare.com",
                passwordHash = "password123",
                phone = "+256701234567",
                age = 38,
                gender = "Female"
            )
            val caregiver2 = Caregiver(
                fullName = "Michael Okello",
                email = "michael@eldercare.com",
                passwordHash = "mike2025",
                phone = "+256772345678",
                age = 42,
                gender = "Male"
            )
            val caregiver3 = Caregiver(
                fullName = "Amina Nakato",
                email = "amina@eldercare.com",
                passwordHash = "amina123",
                phone = "+256755987654",
                age = 35,
                gender = "Female"
            )

            val c1 = dao.insertCaregiver(caregiver1).toInt()
            val c2 = dao.insertCaregiver(caregiver2).toInt()
            val c3 = dao.insertCaregiver(caregiver3).toInt()

            // ELDERLY
            val e1 = dao.insertElderly(
                Elderly(caregiverId = c1, fullName = "Akram Kasozi", age = 72, gender = "Male", phone = "+2567551234567")
            ).toInt()

            val e2 = dao.insertElderly(
                Elderly(caregiverId = c1, fullName = "Fatuma Nansubuga", age = 68, gender = "Female", phone = "+2567551112222")
            ).toInt()

            val e3 = dao.insertElderly(
                Elderly(caregiverId = c2, fullName = "James Mukasa", age = 78, gender = "Male", phone = "+2567729998888")
            ).toInt()

            val e4 = dao.insertElderly(
                Elderly(caregiverId = c3, fullName = "Rose Namutebi", age = 70, gender = "Female", phone = "+2567555554444")
            ).toInt()

            // ACTIVITIES
            val today = java.time.LocalDate.now().toString()

            dao.insertActivity(
                ActivityEntity(
                    elderlyId = e1,
                    caregiverId = c1,
                    title = "Morning Medication",
                    description = "Take blood pressure pills",
                    date = today,
                    time = "08:00",
                    type = "MEDICATION",
                    priority = "HIGH",
                    isCompleted = true
                )
            )

            dao.insertActivity(
                ActivityEntity(
                    elderlyId = e1,
                    caregiverId = c1,
                    title = "Physical Therapy",
                    description = "Leg exercises",
                    date = today,
                    time = "10:30",
                    type = "EXERCISE",
                    priority = "NORMAL",
                    isCompleted = false
                )
            )

            dao.insertActivity(
                ActivityEntity(
                    elderlyId = e2,
                    caregiverId = c1,
                    title = "Breakfast",
                    description = "Diabetic meal",
                    date = today,
                    time = "08:30",
                    type = "MEAL",
                    priority = "NORMAL",
                    isCompleted = true
                )
            )

            dao.insertActivity(
                ActivityEntity(
                    elderlyId = e3,
                    caregiverId = c2,
                    title = "Doctor Appointment",
                    description = "Monthly checkup",
                    date = today,
                    time = "14:00",
                    type = "DOCTOR_VISIT",
                    priority = "HIGH",
                    isCompleted = false
                )
            )

            dao.insertActivity(
                ActivityEntity(
                    elderlyId = e4,
                    caregiverId = c3,
                    title = "Social Time",
                    description = "Family video call",
                    date = today,
                    time = "16:00",
                    type = "SOCIAL",
                    priority = "LOW",
                    isCompleted = false
                )
            )
        }
    }
}
