package old.people.elderly_care.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * Real DataStore implementation of UserPreferences.
 */
class UserPreferences(context: Context) : UserPreferencesContract {

    private val dataStore = context.dataStore

    companion object {
        val LAST_SYNC = longPreferencesKey("last_sync")
    }

    // Expose lastSync as Flow<Long>
    override val lastSync: Flow<Long> = dataStore.data.map {
        it[LAST_SYNC] ?: 0L
    }

    // Update the timestamp
    override suspend fun setLastSync(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[LAST_SYNC] = timestamp
        }
    }
}
