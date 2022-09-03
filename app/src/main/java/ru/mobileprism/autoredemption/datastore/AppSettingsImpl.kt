package ru.mobileprism.autoredemption.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppSettingsImpl(private val context: Context): AppSettings {

    // to make sure there's only one instance
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("appSettings")

        private val DEBUG_MODE = stringPreferencesKey("DEBUG_MODE_KEY")

    }

    override val appSettings: Flow<AppSettingsEntity> = context.dataStore.data
        .map { preferences ->
            Gson().fromJson(preferences[DEBUG_MODE], AppSettingsEntity::class.java) ?: AppSettingsEntity()
        }

    override suspend fun saveAppSettings(appSettings: AppSettingsEntity) {
        context.dataStore.edit { preferences ->
            preferences[DEBUG_MODE] = Gson().toJson(appSettings)
        }
    }

}