package ru.mobileprism.autoredemption.model.datastore

import kotlinx.coroutines.flow.Flow
import ru.mobileprism.autoredemption.compose.screens.auth.AuthState

interface UserDatastore {
    val authState: Flow<AuthState>

    val getCurrentUser: Flow<UserEntity>
    suspend fun saveCurrentUser(user: UserEntity?)


    val getUserToken: Flow<String?>
    suspend fun saveUserToken(token: String?)
}
