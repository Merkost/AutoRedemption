package ru.mobileprism.autobot.viewmodels

import ru.mobileprism.autobot.model.datastore.UserDatastore
import ru.mobileprism.autobot.model.datastore.UserEntity

class AuthManagerImpl(private val userDatastore: UserDatastore): AuthManager {

    override suspend fun saveUserWithToken(user: UserEntity, token: String) {
        userDatastore.saveCurrentUser(user)
        userDatastore.saveUserToken(token)
    }

    override suspend fun loginTestUser() {
        userDatastore.saveUserToken("test")
        userDatastore.saveCurrentUser(UserEntity())

    }

    override suspend fun logout() {
        userDatastore.saveUserToken(null)
    }

}