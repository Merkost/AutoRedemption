package ru.mobileprism.autoredemption.datastore

import kotlinx.coroutines.flow.Flow


interface AppSettings {
    val appSettings: Flow<AppSettingsEntity>
    suspend fun saveAppSettings(appSettings: AppSettingsEntity)
}