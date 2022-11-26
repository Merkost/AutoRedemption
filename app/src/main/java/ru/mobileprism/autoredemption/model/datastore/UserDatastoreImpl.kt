package ru.mobileprism.autoredemption.model.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mobileprism.autoredemption.fragment.UserFragment
import ru.mobileprism.autoredemption.model.entities.AuthState
import ru.mobileprism.autoredemption.viewmodels.Mapper

class UserDatastoreImpl(private val context: Context) : UserDatastore {

    // to make sure there's only one instance
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userSettings")

        private val CURRENT_USER = stringPreferencesKey("USER_KEY")
        private val TOKEN = stringPreferencesKey("TOKEN_KEY")

    }

    override val authState: Flow<AuthState> = context.dataStore.data
        .map { preferences ->
            val user = Gson().fromJson(preferences[CURRENT_USER], UserEntity::class.java)
            val token = preferences[TOKEN]
            if (token == null) AuthState.NotLogged
            else AuthState.Logged(user)
        }

    override val getCurrentUser: Flow<UserEntity> = context.dataStore.data
        .map { preferences ->
            Gson().fromJson(preferences[CURRENT_USER], UserEntity::class.java) ?: UserEntity()
        }


    override val getUserToken: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[TOKEN] }


    override suspend fun saveCurrentUser(user: UserEntity) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_USER] = Gson().toJson(user)
        }
    }

    override suspend fun saveCurrentUser(user: UserFragment) =
        saveCurrentUser(Mapper.mapDbUser(user))

    override suspend fun saveUserToken(token: String?) {
        context.dataStore.edit { preferences ->
            if (token == null) {
                preferences.remove(TOKEN)
            } else {
                preferences[TOKEN] = token
            }
        }
    }

}