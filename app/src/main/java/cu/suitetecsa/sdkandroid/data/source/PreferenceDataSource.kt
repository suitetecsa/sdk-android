package cu.suitetecsa.sdkandroid.data.source

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cu.suitetecsa.sdkandroid.domain.model.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val BALANCE_PREFERENCES_NAME = "balance_preferences"
private val Context.dataStore by preferencesDataStore(name = BALANCE_PREFERENCES_NAME)

class PreferenceDataSource(private val context: Context) {
    fun preferences(): Flow<Preferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val currentSimCardId = preferences[PreferencesKeys.CURRENT_SIM_CARD_ID] ?: ""
            Preferences(currentSimCardId = currentSimCardId)
        }

    suspend fun updateCurrentSimCardId(id: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_SIM_CARD_ID] = id
        }
    }
}

private object PreferencesKeys {
    val CURRENT_SIM_CARD_ID = stringPreferencesKey("CURRENT_SIM_CARD_ID")
}
