package old.people.elderly_care

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import old.people.elderly_care.data.*
import old.people.elderly_care.models.DailyActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private lateinit var dao: FakeAppDao
    private lateinit var prefs: FakeUserPreferences
    private lateinit var viewModel: DashboardViewModel

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        dao = FakeAppDao()
        prefs = FakeUserPreferences()

        val caregiverId = 1

        // Add caregiver
        dao.caregivers.add(
            Caregiver(
                id = caregiverId,
                fullName = "Sarah Johnson",
                email = "sarah@test.com",
                passwordHash = "123",
                phone = "000",
                age = 40,
                gender = "Female"
            )
        )

        // Add elderly
        dao.elderly.add(
            Elderly(
                id = 10,
                caregiverId = caregiverId,
                fullName = "Akram",
                age = 72,
                gender = "Male",
                phone = "111"
            )
        )

        // Add activity
        dao.activities.add(
            ActivityEntity(
                id = 100,
                elderlyId = 10,
                caregiverId = caregiverId,
                title = "Medication",
                description = "Blood pressure pills",
                date = LocalDate.now().toString(),
                time = "08:00",
                type = ActivityType.MEDICATION.name,   
                priority = Priority.NORMAL.name,       
                isCompleted = false
            )
        )

        viewModel = DashboardViewModel(
            dao = dao,
            prefs = prefs,
            caregiverId = caregiverId,
            context = null
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadActivities_correctCounts() = runTest {
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(1, state.totalCount)
        assertEquals(1, state.elderlyCount)
        assertEquals("Akram", state.elderlyNames[10])
    }

    @Test
    fun toggleActivityCompletion_marksCompleted() = runTest {
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleActivityCompletion(100)
        dispatcher.scheduler.advanceUntilIdle()

        val updated = viewModel.uiState.value.activities.first()
        assertTrue(updated.isCompleted)
    }

    @Test
    fun addActivity_increasesTotalCount() = runTest {
        dispatcher.scheduler.advanceUntilIdle()

        val newActivity = DailyActivity(
            id = 0,
            elderlyId = 10,
            title = "Doctor Visit",
            time = "10:00",
            description = "General checkup",
            type = ActivityType.DOCTOR_VISIT,
            isCompleted = false,
            priority = Priority.NORMAL,
            date = LocalDate.now().toString(),
            caregiverId = 1
        )

        viewModel.addActivity(newActivity)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.totalCount)
    }

    @Test
    fun deleteActivity_removesFromState() = runTest {
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteActivity(100)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.totalCount)
    }

    @Test
    fun refreshActivities_updatesLastSync() = runTest {
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.refreshActivities()
        dispatcher.scheduler.advanceUntilIdle()

        val last = prefs.lastSync.first()
        assertTrue(last > 0)
    }
}
