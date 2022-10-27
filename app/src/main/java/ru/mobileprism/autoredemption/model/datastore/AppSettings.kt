package ru.mobileprism.autoredemption.model.datastore

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.type.User
import java.time.LocalDateTime


interface AppSettings {

    val appSettingsEntity: Flow<AppSettingsEntity>
    val getCurrentUserNullable: Flow<UserEntity?>
    val getCurrentUser: Flow<UserEntity>
    val getUserToken: Flow<String>
    //val smsSettings: Flow<SmsSettingsEntity>
    val lastTimeSmsSent: Flow<LocalDateTime>
    val messagesDelay: Flow<Long>
    suspend fun saveCurrentUser(user: UserEntity)
    suspend fun saveUserToken(token: String)
    suspend fun saveMessagesDelay(timeMillis: Long)
    suspend fun saveLastTimeSmsSent(dateTime: LocalDateTime)
    suspend fun saveTestNumbers(numbers: Set<String>)
    suspend fun saveAppSettings(appSettings: AppSettingsEntity)
}