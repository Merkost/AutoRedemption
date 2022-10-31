package ru.mobileprism.autoredemption.viewmodels

import kotlinx.coroutines.flow.Flow
import ru.mobileprism.autoredemption.model.datastore.UserEntity

interface AuthManager {
    suspend fun saveUserWithToken(user: UserEntity, token: String)
    suspend fun loginTestUser()
    suspend fun logout()
}
