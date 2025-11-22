package old.people.elderly_care.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Fake version for unit tests (no DataStore, no Android).
 * Implements the same contract as the real UserPreferences.
 */
class FakeUserPreferences(
    initialValue: Long = 0L
) : UserPreferencesContract {

    private val _lastSync = MutableStateFlow(initialValue)

    override val lastSync: Flow<Long> = _lastSync

    override suspend fun setLastSync(timestamp: Long) {
        _lastSync.value = timestamp
    }
}
