package ru.mobileprism.autoredemption.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.datastore.UserEntity

class ProfileViewModel(private val authManager: AuthManager): ViewModel() {
    fun logout() {
        viewModelScope.launch {
            authManager.logout()
        }
    }

    val currentUser = UserEntity()

}