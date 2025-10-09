
package com.example.elderly_care.data

import com.example.elderly_care.ActivityType
import com.example.elderly_care.DailyActivity
import com.example.elderly_care.Priority

/**
 * Repository for activity data
 * In a real app, this would fetch from database/API
 */
object ActivityRepository {
    
    fun getDefaultActivities(): List<DailyActivity> {
        return listOf(
            DailyActivity("1", "Morning Medication", "08:00 AM", "Blood pressure pills + Vitamin D", ActivityType.MEDICATION, priority = Priority.HIGH),
            DailyActivity("2", "Doctor Appointment", "10:30 AM", "Dr. Smith - Regular checkup", ActivityType.DOCTOR_VISIT, priority = Priority.HIGH),
            DailyActivity("3", "Breakfast", "09:00 AM", "Balanced meal with fiber", ActivityType.MEAL),
            DailyActivity("4", "Morning Walk", "11:30 AM", "20 minute walk outside", ActivityType.EXERCISE),
            DailyActivity("5", "Lunch Time", "12:30 PM", "Low sodium meal with vegetables", ActivityType.MEAL),
            DailyActivity("6", "Afternoon Medication", "02:00 PM", "Diabetes medication", ActivityType.MEDICATION, priority = Priority.HIGH),
            DailyActivity("7", "Physical Therapy", "03:00 PM", "Session with therapist", ActivityType.THERAPY),
            DailyActivity("8", "Light Exercise", "03:30 PM", "15 minute stretching", ActivityType.EXERCISE),
            DailyActivity("9", "Snack Time", "04:30 PM", "Healthy snack", ActivityType.MEAL),
            DailyActivity("10", "Evening Medication", "06:00 PM", "Blood pressure pills", ActivityType.MEDICATION, priority = Priority.HIGH),
            DailyActivity("11", "Dinner Time", "07:00 PM", "Balanced meal", ActivityType.MEAL),
            DailyActivity("12", "Family Call", "08:00 PM", "Video call with Sarah", ActivityType.SOCIAL),
            DailyActivity("13", "Evening Relaxation", "08:30 PM", "Reading or music", ActivityType.OTHER)
        )
    }
}

