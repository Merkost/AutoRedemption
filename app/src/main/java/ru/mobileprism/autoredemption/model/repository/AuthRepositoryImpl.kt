package ru.mobileprism.autoredemption.model.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.VerifyPhoneMutation
import ru.mobileprism.autoredemption.model.ServerError

class AuthRepositoryImpl(private val apolloClient: ApolloClient): AuthRepository {

    override suspend fun verifyPhone(phone: String) = flow {
        val response = apolloClient.mutation(VerifyPhoneMutation(phone)).execute()

        response.data?.verifyPhone?.let {
            emit(Result.success(response.data?.verifyPhone!!))
        } ?: emit(Result.failure(Throwable(response.errors?.first().toString())))

    }.catch { error -> emit(Result.failure(ServerError(error.message))) }

    override suspend fun confirmSms(phone: String, smsCode: String) = flow {
        val response = apolloClient.mutation(ConfirmSmsMutation(phone, smsCode)).execute()

        response.data?.confirmSms?.let {
            emit(Result.success(response.data?.confirmSms!!))
        } ?: emit(Result.failure(Throwable(response.errors?.first().toString())))

    }.catch { error -> emit(Result.failure(ServerError(error.message))) }


}