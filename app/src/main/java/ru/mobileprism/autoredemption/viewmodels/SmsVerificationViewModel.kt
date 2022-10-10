package ru.mobileprism.autoredemption.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mobileprism.autoredemption.model.entities.PhoneAuthEntity
import ru.mobileprism.autoredemption.model.entities.SmsConfirmEntity
import ru.mobileprism.autoredemption.model.repository.AuthRepository
import ru.mobileprism.autoredemption.utils.BaseViewState
import ru.mobileprism.autoredemption.utils.Constants.SMS_RESEND_AWAIT

class SmsVerificationViewModel(
    private val phoneAuth: PhoneAuthEntity,
    private val authRepository: AuthRepository,
) : ViewModel() {

    /*private var phoneAuth: MutableState<PhoneAuthEntity> = mutableStateOf(phoneAuth)*/

    private val _smsCode = MutableStateFlow(phoneAuth.password)
    val smsCode = _smsCode.asStateFlow()

    private val _retrySecs: MutableStateFlow<Int> = MutableStateFlow(SMS_RESEND_AWAIT)
    val retrySecs = _retrySecs.asStateFlow()

    private val _uiState = MutableStateFlow<BaseViewState<SmsConfirmEntity>?>(null)
    val uiState = _uiState.asStateFlow()

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
            if (newValue.length == 6) login()
        }
    }

    fun resetSmsCode() {
        _smsCode.value = ""
    }

    fun login() {
        val currentSmsCode = smsCode.value
        _uiState.value = BaseViewState.Loading()
        viewModelScope.launch {
            authRepository.confirmSms(phoneAuth.phone, currentSmsCode).single().fold(
                onSuccess = {
                    // TODO:
                },
                onFailure = {
                    // TODO:
                }
            )
        }
    }

    fun retry() {
        viewModelScope.launch {

        }
    }

    fun onTextChanged(newChar: String, index: Int) {
        val newCode = _smsCode.value.let {
            StringBuilder(it).also { it.setCharAt(index, newChar.lastOrNull() ?: ' ') }.toString()
        }
        _smsCode.value = newCode
    }


}