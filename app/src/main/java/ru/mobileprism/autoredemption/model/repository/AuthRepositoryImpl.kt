package ru.mobileprism.autoredemption.model.repository

import com.apollographql.apollo3.ApolloClient
import kotlinx.coroutines.flow.flow
import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.VerifyPhoneMutation
import ru.mobileprism.autoredemption.model.datastore.UserDatastore

class AuthRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val userDatastore: UserDatastore
) : AuthRepository {

    override suspend fun verifyPhone(phone: String) = flow {
        val result = safeGraphQLCall { apolloClient.mutation(VerifyPhoneMutation(phone)).execute() }
        emit(result)
    }

    override suspend fun confirmSms(phone: String, smsCode: String) = flow {
        val result = safeGraphQLCall {
            apolloClient.mutation(
                ConfirmSmsMutation(
                    phone,
                    smsCode
                    /*, appType: String = "android"*/
                )
            ).execute()
        }
        if (result is ResultWrapper.Success) {
            result.data.confirmSms?.user?.userFragment?.let {
                userDatastore.saveCurrentUser(it)
            }
            result.data.confirmSms?.token?.let {
                userDatastore.saveUserToken(it)
            }
        }
        emit(result)
    }


}