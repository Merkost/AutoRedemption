package ru.mobileprism.autoredemption.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.R
import ru.mobileprism.autoredemption.model.ServerError
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.entities.PhoneAuthEntity
import ru.mobileprism.autoredemption.model.repository.AuthRepository
import ru.mobileprism.autoredemption.utils.BaseViewState
import ru.mobileprism.autoredemption.utils.Constants.DEFAULT_MESSAGES_DELAY
import ru.mobileprism.autoredemption.utils.Constants.RETRY_DELAY
import ru.mobileprism.autoredemption.utils.Constants.SMS_RESEND_AWAIT

class SmsVerificationViewModel(
    private val authManager: AuthManager,
    private val phoneAuth: PhoneAuthEntity,
    private val authRepository: AuthRepository,
) : ViewModel() {

    /*private var phoneAuth: MutableState<PhoneAuthEntity> = mutableStateOf(phoneAuth)*/

    private val _smsCode = MutableStateFlow(phoneAuth.password)
    val smsCode = _smsCode.asStateFlow()

    private val _retrySecs: MutableStateFlow<Int> = MutableStateFlow(SMS_RESEND_AWAIT)
    val retrySecs = _retrySecs.asStateFlow()

    private val _uiState = MutableStateFlow<BaseViewState<ConfirmSmsMutation.ConfirmSms>?>(null)
    val uiState = _uiState.asStateFlow()

    private var loginJob: Job? = null

    init { setCountDown() }

    private fun setCountDown() {
        viewModelScope.launch {
            (SMS_RESEND_AWAIT downTo 0).asFlow()
                //.onStart { retrySecs.value = totalSeconds }
                .onEach { delay(1000) }
                //.onCompletion { retrySecs.value = 0 }
                .collect { remainingSeconds: Int ->
                    _retrySecs.update { remainingSeconds }
                }
        }
    }

    val isError: MutableState<Boolean>
        get() {
            return mutableStateOf(if (smsCode.value.length == 6) !isNewSmsCodeValid(smsCode.value) else false)
        }

    private fun isNewSmsCodeValid(newValue: String): Boolean {
        return when (newValue.length) {
            0 -> true
            in 1..6 -> newValue.toIntOrNull() != null
            else -> false
        }
    }

    fun onSmsCodeValueChange(newValue: String) {
        //_smsCode.value = newValue
        if (isNewSmsCodeValid(newValue)) {
            _smsCode.value = newValue
            if (newValue.length == 6) {
                _uiState.value = BaseViewState.Loading()
                login()
            }
        }
    }

    fun resetSmsCode() {
        _smsCode.value = ""
    }

    fun login()  {
        _uiState.value = BaseViewState.Loading()
        checkSmsCode()
    }

    private fun checkSmsCode() {
        val currentSmsCode = smsCode.value
        loginJob = viewModelScope.launch {
            authRepository.confirmSms(phoneAuth.phone, currentSmsCode).single().fold(
                onSuccess = { result ->
                    authManager.saveUserWithToken(UserMapper.mapDbUser(result.user), result.token)
                    _uiState.update { BaseViewState.Success(result) }
                },
                onFailure = { error ->
                    if (error is ServerError) {
                        _uiState.update { BaseViewState.Error(error.message, stringRes = R.string.server_error) }
                    } else {
                        // TODO:
                        _uiState.update { BaseViewState.Error(error.message) }
                    }
                }
            )
        }
    }

    fun retry() {
        viewModelScope.launch {
            _uiState.value = BaseViewState.Loading()
            delay(RETRY_DELAY)
            // TODO: properly resend sms
            checkSmsCode()
        }
    }

    fun onTextChanged(newChar: String, index: Int) {
        val newCode = _smsCode.value.let {
            StringBuilder(it).also { it.setCharAt(index, newChar.lastOrNull() ?: ' ') }.toString()
        }
        _smsCode.value = newCode
    }

    fun cancelLoading() {
        loginJob?.cancel()
    }

    fun resetState() {
        _uiState.update { null }
    }


}