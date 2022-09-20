package ru.mobileprism.autoredemption.model.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mobileprism.autoredemption.utils.Constants
import java.time.LocalDateTime

class AppSettingsImpl(private val context: Context) : AppSettings {

    // to make sure there's only one instance
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("appSettings")

        private val APP_SETTINGS = stringPreferencesKey("APP_SETTINGS_KEY")
        private val TEST_NUMBERS = stringSetPreferencesKey("TEST_NUMBERS_KEY")
        private val LAST_TIME_SAVED = stringPreferencesKey("LAST_TIME_SAVED_KEY")
        private val MESSAGES_DELAY = longPreferencesKey("MESSAGES_DELAY_KEY")


    }

    override val appSettings: Flow<AppSettingsEntity> = context.dataStore.data
        .map { preferences ->
            Gson().fromJson(preferences[APP_SETTINGS], AppSettingsEntity::class.java)
                ?: AppSettingsEntity()
        }

    override suspend fun saveAppSettings(appSettings: AppSettingsEntity) {
        context.dataStore.edit { preferences ->
            preferences[APP_SETTINGS] = Gson().toJson(appSettings)
        }
    }


    override val testNumbers: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[TEST_NUMBERS] ?: emptySet()
        }

    override val lastTimeSmsSent: Flow<LocalDateTime> = context.dataStore.data
        .map { preferences ->
            kotlin.runCatching { LocalDateTime.parse(preferences[LAST_TIME_SAVED]) }
                .getOrNull() ?: LocalDateTime.now().minusDays(1)
        }
    override val messagesDelay: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[MESSAGES_DELAY] ?: Constants.DEFAULT_MESSAGES_DELAY
        }

    override suspend fun saveMessagesDelay(timeMillis: Long) {
        context.dataStore.edit { preferences ->
            preferences[MESSAGES_DELAY] = timeMillis
        }
    }

    override suspend fun saveLastTimeSmsSent(dateTime: LocalDateTime) {
        context.dataStore.edit { preferences ->
            preferences[LAST_TIME_SAVED] = dateTime.toString()
        }
    }

    override suspend fun saveTestNumbers(numbers: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[TEST_NUMBERS] = numbers
        }
    }

}