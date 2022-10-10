package ru.mobileprism.autoredemption.model.repository

import kotlinx.coroutines.flow.Flow
import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.VerifyPhoneMutation

interface AuthRepository {
    suspend fun verifyPhone(phone: String): Flow<Result<VerifyPhoneMutation.VerifyPhone>>
    suspend fun confirmSms(phone: String, smsCode: String): Flow<Result<ConfirmSmsMutation.ConfirmSms>>
}