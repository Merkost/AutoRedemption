package ru.mobileprism.autoredemption.model.repository

import com.apollographql.apollo3.ApolloClient
import kotlinx.coroutines.flow.flow
import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.VerifyPhoneMutation

class AuthRepositoryImpl(private val apolloClient: ApolloClient) : AuthRepository {

    override suspend fun verifyPhone(phone: String) = flow {
        val result = safeGraphQLCall { apolloClient.mutation(VerifyPhoneMutation(phone)).execute() }
        emit(result)
    }


    override suspend fun confirmSms(phone: String, smsCode: String) = flow {
        val result = safeGraphQLCall {
            // TODO: Add appType to confirmSMS
            apolloClient.mutation(ConfirmSmsMutation(phone, smsCode/*, appType: String = "android"*/)).execute()
        }
        emit(result)
    }


}