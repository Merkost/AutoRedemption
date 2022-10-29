package ru.mobileprism.autoredemption.viewmodels

import ru.mobileprism.autoredemption.model.datastore.UserEntity

interface AuthManager {
    suspend fun saveUserWithToken(user: UserEntity, token: String)
    suspend fun loginTestUser()
    suspend fun logout()
}
