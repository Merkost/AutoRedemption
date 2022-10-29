package ru.mobileprism.autoredemption.viewmodels

import kotlinx.coroutines.flow.Flow
import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.VerifyPhoneMutation
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.datastore.UserEntity

class AuthManagerImpl(private val appSettings: AppSettings): AuthManager {

    private val testUser = UserEntity()

    override suspend fun saveUserWithToken(user: UserEntity, token: String) {
        appSettings.saveCurrentUser(user)
        appSettings.saveUserToken(token)
    }

    override suspend fun loginTestUser() {
        appSettings.saveCurrentUser(testUser)
        appSettings.saveUserToken("")
    }

    override suspend fun logout() {
        appSettings.saveCurrentUser(null)
        appSettings.saveUserToken("")
    }

}