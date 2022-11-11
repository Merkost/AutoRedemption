package ru.mobileprism.autoredemption.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mobileprism.autoredemption.R
import ru.mobileprism.autoredemption.model.ServerError
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.datastore.AppSettingsEntity
import ru.mobileprism.autoredemption.model.entities.PhoneAuthEntity
import ru.mobileprism.autoredemption.model.repository.AuthRepository
import ru.mobileprism.autoredemption.model.repository.fold
import ru.mobileprism.autoredemption.utils.BaseViewState
import ru.mobileprism.autoredemption.utils.Constants
import ru.mobileprism.autoredemption.utils.Constants.PHONE_DEFAULT_VALUE
import ru.mobileprism.autoredemption.utils.extractDigits
import java.util.regex.Matcher
import java.util.regex.Pattern

class PhoneEnteringViewModel(
    private val authRepository: AuthRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<BaseViewState<PhoneAuthEntity>?>(null)
    val uiState = _uiState.asStateFlow()

    private val _phoneNum = MutableStateFlow(PHONE_DEFAULT_VALUE)
    val phoneNum = _phoneNum.asStateFlow()

    val isPhoneError = phoneNum.map {
        if (phoneNum.value.length >= 12) !Constants.phoneRegex.matches(phoneNum.value) else false
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val isPhoneSucceed = phoneNum.map {
        /*it.length == 12 && isPhoneNumValid(it)*/true
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

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

    private var authJob: Job? = null

    fun resetPhoneNum() {
        _phoneNum.update { "+7" }
    }

    fun authenticate() {
        authJob = viewModelScope.launch {
            val phoneNumber = phoneNum.value
            _uiState.update { BaseViewState.Loading() }
            authRepository.verifyPhone(phoneNumber).single().fold(
                onSuccess = { data ->
                    val code = data.verifyPhone?.message ?: ""

                    _uiState.update {
                        BaseViewState.Success(
                            PhoneAuthEntity(
                                password = code,
                                phone = phoneNumber
                            )
                        )
                    }
                },
                onError = { errorRes ->
                    _uiState.update { BaseViewState.Error(stringRes = errorRes) }
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = null
    }

    fun cancelLoading() {
        authJob?.cancel()
        resetState()
    }



    fun loginTestUser() {
        viewModelScope.launch {
            authManager.loginTestUser()
        }
    }
}