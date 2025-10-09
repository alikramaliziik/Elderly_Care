package com.example.elderly_care

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for DashboardViewModel
 * Tests ViewModel logic and state management
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    
    private lateinit var viewModel: DashboardViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        // Set test dispatcher
        Dispatchers.setMain(testDispatcher)
        viewModel = DashboardViewModel()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state is loading`() {
        // Given - fresh ViewModel
        val initialState = viewModel.uiState.value
        
        // Then - should be loading
        assertTrue(initialState.isLoading)
        assertTrue(initialState.activities.isEmpty())
    }
    
    @Test
    fun `activities load successfully`() = runTest {
        // When - wait for loading to complete
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        
        // Then - activities should be loaded
        assertFalse(state.isLoading)
        assertTrue(state.activities.isNotEmpty())
        assertEquals(13, state.totalCount)
        assertNull(state.errorMessage)
    }
    
    @Test
    fun `toggle activity completion updates state`() = runTest {
        // Given - loaded activities
        advanceUntilIdle()
        
        val activityId = "1"
        val initialCompleted = viewModel.uiState.value.completedCount
        
        // When - toggle completion
        viewModel.toggleActivityCompletion(activityId)
        
        val updatedState = viewModel.uiState.value
        val activity = updatedState.activities.find { it.id == activityId }
        
        // Then - completion status should change
        assertNotNull(activity)
        assertTrue(activity!!.isCompleted)
        assertEquals(initialCompleted + 1, updatedState.completedCount)
    }
    
    @Test
    fun `toggle twice returns to original state`() = runTest {
        // Given
        advanceUntilIdle()
        val activityId = "1"
        val initialState = viewModel.uiState.value
        
        // When - toggle twice
        viewModel.toggleActivityCompletion(activityId)
        viewModel.toggleActivityCompletion(activityId)
        
        val finalState = viewModel.uiState.value
        val activity = finalState.activities.find { it.id == activityId }
        
        // Then - should be back to original
        assertFalse(activity!!.isCompleted)
        assertEquals(initialState.completedCount, finalState.completedCount)
    }
    
    @Test
    fun `pending medication count updates correctly`() = runTest {
        // Given
        advanceUntilIdle()
        
        // Count medication activities
        val medicationActivities = viewModel.uiState.value.activities.filter {
            it.type == ActivityType.MEDICATION
        }
        
        val initialPendingCount = viewModel.uiState.value.pendingMedicationCount
        
        // When - complete a medication
        val firstMedication = medicationActivities.first()
        viewModel.toggleActivityCompletion(firstMedication.id)
        
        val updatedState = viewModel.uiState.value
        
        // Then - pending count should decrease
        assertEquals(initialPendingCount - 1, updatedState.pendingMedicationCount)
    }
    
    @Test
    fun `completion percentage calculates correctly`() = runTest {
        // Given
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        val expected = state.completedCount.toFloat() / state.totalCount
        
        // Then
        assertEquals(expected, state.completionPercentage, 0.01f)
    }
    
    @Test
    fun `add activity increases total count`() = runTest {
        // Given
        advanceUntilIdle()
        val initialCount = viewModel.uiState.value.totalCount
        
        val newActivity = DailyActivity(
            id = "14",
            title = "New Activity",
            time = "10:00 PM",
            description = "Test activity",
            type = ActivityType.OTHER
        )
        
        // When
        viewModel.addActivity(newActivity)
        
        val updatedState = viewModel.uiState.value
        
        // Then
        assertEquals(initialCount + 1, updatedState.totalCount)
        assertTrue(updatedState.activities.contains(newActivity))
    }
    
    @Test
    fun `delete activity decreases total count`() = runTest {
        // Given
        advanceUntilIdle()
        val initialCount = viewModel.uiState.value.totalCount
        val activityToDelete = viewModel.uiState.value.activities.first()
        
        // When
        viewModel.deleteActivity(activityToDelete.id)
        
        val updatedState = viewModel.uiState.value
        
        // Then
        assertEquals(initialCount - 1, updatedState.totalCount)
        assertFalse(updatedState.activities.any { it.id == activityToDelete.id })
    }
    
    @Test
    fun `clear error removes error message`() = runTest {
        // This test would need error injection in real scenario
        // For now, just verify the method exists and works
        viewModel.clearError()
        
        val state = viewModel.uiState.value
        assertNull(state.errorMessage)
        assertFalse(state.hasError)
    }
}

