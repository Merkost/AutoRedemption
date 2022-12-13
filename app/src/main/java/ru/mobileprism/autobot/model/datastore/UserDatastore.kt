package ru.mobileprism.autobot.model.datastore

import kotlinx.coroutines.flow.Flow
import ru.mobileprism.autobot.fragment.UserFragment
import ru.mobileprism.autobot.model.entities.AuthState

interface UserDatastore {
    val authState: Flow<AuthState>

    val getCurrentUser: Flow<UserEntity>
    suspend fun saveCurrentUser(user: UserEntity)
    suspend fun saveCurrentUser(user: UserFragment)



    val getUserToken: Flow<String?>
    suspend fun saveUserToken(token: String?)
}
