package ru.mobileprism.autoredemption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import ru.mobileprism.autoredemption.compose.screens.auth.AuthState
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.datastore.UserEntity

class MainActivityViewModel(appSettings: AppSettings) : ViewModel() {

    val currentUserNullable =
        appSettings.getCurrentUserNullable.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val currentUser =
        appSettings.getCurrentUser.stateIn(viewModelScope, SharingStarted.Eagerly, UserEntity())

    val authState: StateFlow<AuthState?> = currentUserNullable.map {
        if (it == null) AuthState.NotLogged else AuthState.Logged
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)


}