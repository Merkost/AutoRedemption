package ru.mobileprism.autoredemption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import ru.mobileprism.autoredemption.model.entities.AuthState
import ru.mobileprism.autoredemption.model.datastore.UserDatastore

class MainActivityViewModel(userDatastore: UserDatastore) : ViewModel() {

    val authState: StateFlow<AuthState?> = userDatastore.authState.take(1)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

}