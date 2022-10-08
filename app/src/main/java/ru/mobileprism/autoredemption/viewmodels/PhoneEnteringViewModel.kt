package ru.mobileprism.autoredemption.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.datastore.AppSettingsEntity
import ru.mobileprism.autoredemption.utils.BaseViewState
import ru.mobileprism.autoredemption.utils.Constants
import ru.mobileprism.autoredemption.utils.Constants.PHONE_DEFAULT_VALUE

class PhoneEnteringViewModel(val appSettings: AppSettings) : ViewModel() {


    private val _uiState = MutableStateFlow<BaseViewState<Any>?>(null)
    val uiState = _uiState.asStateFlow()

    private val _phoneNum = MutableStateFlow(PHONE_DEFAULT_VALUE)
    val phoneNum = _phoneNum.asStateFlow()

    val isPhoneError = phoneNum.map {
        if (phoneNum.value.length >= 12) !Constants.phoneRegex.matches(phoneNum.value) else false
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val isPhoneSucceed = phoneNum.map {
        phoneNum.value.length == 12 && isPhoneNumValid(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private var authJob: Job? = null

    fun onPhoneSet(newValue: String) {
        if (isPhoneNumValid(newValue)) _phoneNum.update { newValue }
    }

    private fun isPhoneNumValid(newValue: String): Boolean {
        return when (newValue.length) {
            in 0..2 -> newValue.startsWith("+7")
            in 3..12 -> {
                newValue.startsWith("+7") && newValue.last().digitToIntOrNull() != null
            }
            else -> false
        }
    }

    fun resetPhoneNum() {
        _phoneNum.update { "+7" }
    }

    fun authenticate() {
        authJob = viewModelScope.launch {
            _uiState.update { BaseViewState.Loading() }
            val currentPhoneNum = phoneNum.value
            /*loginRepository.getPhoneResponse(phone = currentPhoneNum).collect { result ->
                when (result) {
                    is ResultWrapper.NetworkError -> {
                        _uiState.value = BaseViewState.Error(null)
                        showNetworkError()
                    }
                    is ResultWrapper.GenericError-> {
                        _uiState.value = BaseViewState.Error(text = result.data?.description)
                    }
                    is ResultWrapper.Success -> {
                        val regEntity = PhoneRegisterEntity(
                            password = result.data.password ?: "",
                            phone = currentPhoneNum
                        )
                        _uiState.value = BaseViewState.Success(regEntity)
                    }
                }
            }*/
        }
    }

    fun resetState() {
        _uiState.value = null
    }

    fun cancelLoading() {
        authJob?.cancel()
        resetState()
    }
}