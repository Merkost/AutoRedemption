package ru.mobileprism.autoredemption.model.datastore

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime


interface AppSettings {
    val appSettings: Flow<AppSettingsEntity>
    //val smsSettings: Flow<SmsSettingsEntity>
    val testNumbers: Flow<Set<String>>
    val lastTimeSmsSent: Flow<LocalDateTime>
    val messagesDelay: Flow<Long>
    suspend fun saveMessagesDelay(timeMillis: Long)
    suspend fun saveLastTimeSmsSent(dateTime: LocalDateTime)
    suspend fun saveTestNumbers(numbers: Set<String>)
    suspend fun saveAppSettings(appSettings: AppSettingsEntity)
}