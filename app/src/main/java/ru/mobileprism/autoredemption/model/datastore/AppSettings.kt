package ru.mobileprism.autoredemption.model.datastore

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime


interface AppSettings {

    val appSettingsEntity: Flow<AppSettingsEntity>
    val selectedSimId: Flow<Int?>
    //val smsSettings: Flow<SmsSettingsEntity>
    val lastTimeSmsSent: Flow<LocalDateTime>
    val messagesDelay: Flow<Long>

    suspend fun saveSelectedSimId(selectedSimId: Int)
    suspend fun saveMessagesDelay(timeMillis: Long)
    suspend fun saveLastTimeSmsSent(dateTime: LocalDateTime)
    suspend fun saveTestNumbers(numbers: Set<String>)
    suspend fun saveAppSettings(appSettings: AppSettingsEntity)
}