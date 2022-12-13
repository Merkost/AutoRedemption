package ru.mobileprism.autobot.model.repository

import kotlinx.coroutines.flow.Flow
import ru.mobileprism.autobot.ConfirmSmsMutation
import ru.mobileprism.autobot.VerifyPhoneMutation

interface AuthRepository {
    suspend fun verifyPhone(phone: String): Flow<ResultWrapper<VerifyPhoneMutation.Data>>
    suspend fun confirmSms(phone: String, smsCode: String): Flow<ResultWrapper<ConfirmSmsMutation.Data>>
}

