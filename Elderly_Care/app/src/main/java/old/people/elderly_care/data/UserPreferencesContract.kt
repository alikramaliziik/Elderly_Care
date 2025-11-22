package old.people.elderly_care.data

import kotlinx.coroutines.flow.Flow

/**
 * Contract used by DashboardViewModel for testing and production.
 */
interface UserPreferencesContract {
    val lastSync: Flow<Long>
    suspend fun setLastSync(timestamp: Long)
}
