package ru.mobileprism.autobot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mobileprism.autobot.R
import ru.mobileprism.autobot.model.entities.PhoneAuthEntity
import ru.mobileprism.autobot.model.repository.AuthRepository
import ru.mobileprism.autobot.model.repository.AutoBotError
import ru.mobileprism.autobot.model.repository.fold
import ru.mobileprism.autobot.utils.BaseViewState
import ru.mobileprism.autobot.utils.Constants
import ru.mobileprism.autobot.utils.Constants.PHONE_DEFAULT_VALUE
import ru.mobileprism.autobot.utils.PhoneNumberFormatter

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
        it.length == 12 && isPhoneNumValid(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun onPhoneSet(newValue: String) {
        if (isPhoneNumValid(newValue)) _phoneNum.update { newValue }
    }

    fun onPhoneSetFromIntent(phone: String) {
        PhoneNumberFormatter.formatToCountryCodeAndNationalNumber(phone)?.let {
            val (code, number) = it
            if (code.toString() == "7" && number.toString().length == 10) {
                _phoneNum.value = "+7$number"
                authenticate()
            } else _uiState.update { BaseViewState.Error(AutoBotError(R.string.invalid_number)) }
        } ?: _uiState.update { BaseViewState.Error(AutoBotError(R.string.invalid_number)) }
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
                onError = { error ->
                    _uiState.update { BaseViewState.Error(error) }
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