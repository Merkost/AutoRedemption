package ru.mobileprism.autobot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.mobileprism.autobot.model.datastore.UserDatastore
import ru.mobileprism.autobot.model.datastore.UserEntity

class ProfileViewModel(
    private val authManager: AuthManager,
    private val userDatastore: UserDatastore
) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            authManager.logout()
        }
    }

    val currentUser = userDatastore.getCurrentUser.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), UserEntity()
    )

}