package ru.mobileprism.autoredemption.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mobileprism.autoredemption.model.entities.PhoneAuthEntity
import ru.mobileprism.autoredemption.model.entities.SmsConfirmEntity
import ru.mobileprism.autoredemption.model.repository.AuthRepository
import ru.mobileprism.autoredemption.model.repository.AutoBotError
import ru.mobileprism.autoredemption.model.repository.fold
import ru.mobileprism.autoredemption.utils.BaseViewState
import ru.mobileprism.autoredemption.utils.Constants.RETRY_DELAY
import ru.mobileprism.autoredemption.utils.Constants.SMS_RESEND_AWAIT
import java.util.regex.Matcher
import java.util.regex.Pattern

class SmsVerificationViewModel(
    private val authManager: AuthManager,
    private val phoneAuth: PhoneAuthEntity,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _smsCode = MutableStateFlow(phoneAuth.password)
    val smsCode = _smsCode.asStateFlow()

    private val _retrySecs: MutableStateFlow<Int> = MutableStateFlow(SMS_RESEND_AWAIT)
    val retrySecs = _retrySecs.asStateFlow()

    private val _uiState = MutableStateFlow<BaseViewState<SmsConfirmEntity>?>(null)
    val uiState = _uiState.asStateFlow()

    private var loginJob: Job? = null

    init {
        setCountDown()
    }

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
        get() = mutableStateOf(if (smsCode.value.length == 6) !isNewSmsCodeValid(smsCode.value) else false)


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
                login()
            }
        }
    }

    fun onSmsCodeFromListener(str: String) {
        val newCode = extractDigits(str)
        onSmsCodeValueChange(newCode)
    }

    fun resetSmsCode() {
        _smsCode.value = ""
    }

    fun login() {
        _uiState.value = BaseViewState.Loading()
        checkSmsCode()
    }

    private fun checkSmsCode() {
        val currentSmsCode = smsCode.value
        loginJob = viewModelScope.launch {
            authRepository.confirmSms(phoneAuth.phone, currentSmsCode).single().fold(
                onSuccess = { result ->
                    result.confirmSms?.let { smsResult ->
                        authManager.saveUserWithToken(Mapper.mapDbUser(smsResult.user.userFragment), smsResult.token)
                        _uiState.update {
                            BaseViewState.Success(
                                SmsConfirmEntity(
                                    token = smsResult.token,
                                    user = Mapper.mapDbUser(smsResult.user.userFragment),
                                )
                            )
                        }
                    } ?: _uiState.update { BaseViewState.Error(AutoBotError.EmptyResponseError) }
                },
                onError = { error ->
                    _uiState.update { BaseViewState.Error(error) }
                }
            )
        }
    }

    fun retrySms() {
        _retrySecs.value = SMS_RESEND_AWAIT
        viewModelScope.launch {
            setCountDown()
            _uiState.value = null
            val result = authRepository.verifyPhone(phoneAuth.phone).single().fold(
                onSuccess = { data ->
                    data.verifyPhone?.message?.let { smsCode ->
                        _smsCode.update { smsCode }
                    }
                },
                onError = { error ->
                    _uiState.update { BaseViewState.Error(error) }
                }
            )
        }
    }

    fun retry() {
        viewModelScope.launch {
            _uiState.value = BaseViewState.Loading()
            delay(RETRY_DELAY)
            retrySms()
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
        resetState()
    }

    fun resetState() {
        _uiState.update { null }
    }

    private fun extractDigits(str: String): String {
        val p: Pattern = Pattern.compile("(\\d{6})")
        val m: Matcher = p.matcher(str)
        return if (m.find()) {
            m.group(0) ?: ""
        } else ""
    }


}