package ru.mobileprism.autoredemption.datastore

import kotlinx.coroutines.flow.Flow


interface AppSettings {
    val appSettings: Flow<AppSettingsEntity>
    //val smsSettings: Flow<SmsSettingsEntity>
    val testNumbers: Flow<Set<String>>
    suspend fun saveTestNumbers(numbers: Set<String>)
    suspend fun saveAppSettings(appSettings: AppSettingsEntity)
}