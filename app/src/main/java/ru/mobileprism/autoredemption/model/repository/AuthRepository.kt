package ru.mobileprism.autoredemption.model.repository

import kotlinx.coroutines.flow.Flow
import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.VerifyPhoneMutation
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface AuthRepository {
    suspend fun verifyPhone(phone: String): Flow<ResultWrapper<VerifyPhoneMutation.Data>>
    suspend fun confirmSms(phone: String, smsCode: String): Flow<ResultWrapper<ConfirmSmsMutation.Data>>
}

