package ru.mobileprism.autoredemption.model.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import kotlinx.coroutines.flow.flow
import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.VerifyPhoneMutation

class AuthRepositoryImpl(val apolloClient: ApolloClient): AuthRepository {




    override suspend fun verifyPhone(phone: String) = flow {
        val response = apolloClient.mutation(VerifyPhoneMutation(phone)).execute()

        if (response.data?.verifyPhone != null) {
            emit(Result.success(response.data?.verifyPhone!!))
        } else {
            emit(Result.failure(Throwable(response.errors?.first().toString())))
        }

        Log.d("VerifyPhoneMutation", "Success ${response.data}")
    }

    override suspend fun confirmSms(phone: String, smsCode: String) = flow {
        val response = apolloClient.mutation(ConfirmSmsMutation(phone, smsCode)).execute()

        if (response.data?.confirmSms != null) {
            emit(Result.success(response.data?.confirmSms!!))
        } else {
            emit(Result.failure(Throwable(response.errors?.first().toString())))
        }

        Log.d("ConfirmSmsMutation", "Success ${response.data}")
    }


}