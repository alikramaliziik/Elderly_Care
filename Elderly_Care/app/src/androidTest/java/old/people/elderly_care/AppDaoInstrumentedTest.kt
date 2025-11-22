package old.people.elderly_care.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDaoInstrumentedTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AppDao

    @Before
    fun setup() = runTest {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        dao = db.dao()

        // FIX: Populate manually—callback does NOT work in tests
        AppDatabase.populateForTest(dao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun caregiversInserted() = runTest {
        val caregivers = dao.getAllCaregivers()
        assertEquals(3, caregivers.size)
    }

    @Test
    fun elderlyInserted() = runTest {
        val elderly = dao.getAllElderly()
        assertEquals(4, elderly.size)
    }

    @Test
    fun activitiesInsertedForToday() = runTest {
        val today = java.time.LocalDate.now().toString()
        val activities = dao.getActivitiesForDate(today)

        assertTrue(activities.isNotEmpty())
    }

    @Test
    fun insertCaregiverWorks() = runTest {
        val newCaregiver = Caregiver(
            fullName = "Test Person",
            email = "test@example.com",
            passwordHash = "pass",
            phone = "111",
            age = 30,
            gender = "Male"
        )

        val id = dao.insertCaregiver(newCaregiver)
        val saved = dao.getCaregiverById(id.toInt())

        assertNotNull(saved)
        assertEquals("Test Person", saved!!.fullName)
    }

    @Test
    fun cascadeDeleteWorks() = runTest {
        val caregiver = dao.getAllCaregivers().first()

        val elderly = Elderly(
            caregiverId = caregiver.id,
            fullName = "Cascade Elder",
            age = 80,
            gender = "Male",
            phone = "222"
        )

        val elderlyId = dao.insertElderly(elderly).toInt()

        dao.insertActivity(
            ActivityEntity(
                elderlyId = elderlyId,
                caregiverId = caregiver.id,
                title = "Test Act",
                description = "desc",
                date = "2025-11-22",
                time = "09:00",
                type = "TEST",
                priority = "LOW",
                isCompleted = false
            )
        )

        dao.deleteElderlyWithActivities(elderlyId)

        assertNull(dao.getElderlyById(elderlyId))
    }

    @Test
    fun activityCountsCorrect() = runTest {
        val elderly = dao.getAllElderly().first()
        val total = dao.getActivityCountForElderly(elderly.id)
        val completed = dao.getCompletedActivityCountForElderly(elderly.id)

        assertTrue(total >= completed)
        assertTrue(total >= 0)
    }
}
