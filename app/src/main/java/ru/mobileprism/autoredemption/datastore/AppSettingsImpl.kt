package ru.mobileprism.autoredemption.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppSettingsImpl (private val context: Context): AppSettings {

    // to make sure there's only one instance
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("appSettings")

        private val APP_SETTINGS = stringPreferencesKey("APP_SETTINGS_KEY")
        private val TEST_NUMBERS = stringSetPreferencesKey("TEST_NUMBERS_KEy")

    }

    override val appSettings: Flow<AppSettingsEntity> = context.dataStore.data
        .map { preferences ->
            Gson().fromJson(preferences[APP_SETTINGS], AppSettingsEntity::class.java) ?: AppSettingsEntity()
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

    override suspend fun saveTestNumbers(numbers: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[TEST_NUMBERS] = numbers
        }
    }

}