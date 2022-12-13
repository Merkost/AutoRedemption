package ru.mobileprism.autobot.viewmodels

import ru.mobileprism.autobot.model.datastore.UserEntity

interface AuthManager {
    suspend fun saveUserWithToken(user: UserEntity, token: String)
    suspend fun loginTestUser()
    suspend fun logout()
}
