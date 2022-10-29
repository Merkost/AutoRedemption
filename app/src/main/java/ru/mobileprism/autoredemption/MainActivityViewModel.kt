package ru.mobileprism.autoredemption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import ru.mobileprism.autoredemption.compose.screens.auth.AuthState
import ru.mobileprism.autoredemption.model.datastore.AppSettings

class MainActivityViewModel(appSettings: AppSettings) : ViewModel() {

    val authState: StateFlow<AuthState?> = appSettings.authState
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

}