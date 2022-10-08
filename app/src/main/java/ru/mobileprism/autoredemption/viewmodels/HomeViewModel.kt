package ru.mobileprism.autoredemption.viewmodels

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.datastore.AppSettingsEntity

class HomeViewModel(val appSettings: AppSettings) : ViewModel() {


    private val appSettingsEntity = MutableStateFlow(AppSettingsEntity())

    init {
        setFlowListeners()
    }

    private fun setFlowListeners() {
        viewModelScope.launch {
            appSettings.appSettingsEntity.collect {
                appSettingsEntity.value = it
            }
        }
    }


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
                    testNumbers = testNumbers + testNumbers.size.toString()
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