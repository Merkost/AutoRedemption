package ru.mobileprism.autoredemption.model.datastore

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDateTime


interface AppSettings {

    val appSettingsEntity: Flow<AppSettingsEntity>
    //val smsSettings: Flow<SmsSettingsEntity>
    val lastTimeSmsSent: Flow<LocalDateTime>
    val messagesDelay: Flow<Long>
    suspend fun saveMessagesDelay(timeMillis: Long)
    suspend fun saveLastTimeSmsSent(dateTime: LocalDateTime)
    suspend fun saveTestNumbers(numbers: Set<String>)
    suspend fun saveAppSettings(appSettings: AppSettingsEntity)
}