package ru.mobileprism.autobot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mobileprism.autobot.model.datastore.AppSettings
import ru.mobileprism.autobot.model.datastore.AppSettingsEntity

class HomeViewModel(val appSettings: AppSettings) : ViewModel() {


    private val appSettingsEntity = appSettings.appSettingsEntity
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettingsEntity())


    fun addRealNumber(number: String) {
        viewModelScope.launch {
            appSettingsEntity.value.let {
                appSettings.saveAppSettings(it.copy(numbers = it.numbers + number))
            }
        }
    }

    fun addTestNumber() {
        viewModelScope.launch {
            appSettingsEntity.value.testNumbers.let { testNumbers ->
                appSettings.saveAppSettings(appSettingsEntity.value.copy(
                    testNumbers = testNumbers + (testNumbers.size + 1).toString()
                ))
            }
        }
    }

    fun deleteTestNumber(number: String) {
        viewModelScope.launch {
            appSettingsEntity.value.let { appSettingsEntity ->
                appSettings.saveAppSettings(appSettingsEntity.copy(
                    testNumbers = appSettingsEntity.testNumbers - number
                ))
            }
        }
    }

    fun deleteRealNumber(number: String) {
        viewModelScope.launch {
            appSettingsEntity.value.let {
                appSettings.saveAppSettings(it.copy(numbers = it.numbers - number))
            }
        }
    }
}