package ru.mobileprism.autoredemption.viewmodels

import ru.mobileprism.autoredemption.model.datastore.UserDatastore
import ru.mobileprism.autoredemption.model.datastore.UserEntity

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
        userDatastore.saveCurrentUser(null)
        userDatastore.saveUserToken(null)
    }

}