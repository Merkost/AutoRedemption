package ru.mobileprism.autoredemption.model.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


suspend fun <T : Operation.Data> safeGraphQLCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> ApolloResponse<T>,
): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            val result = apiCall.invoke()
            when {
                result.hasErrors() -> ResultWrapper.GenericError(AutoBotError(error = result.errors?.firstOrNull()))
                else -> ResultWrapper.Success(result.data!!)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> ResultWrapper.NetworkError
                else -> ResultWrapper.GenericError(null)
            }
        }
    }
}

sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class GenericError(val data: AutoBotError?) : ResultWrapper<Nothing>()
    object NetworkError : ResultWrapper<Nothing>()
}

@OptIn(ExperimentalContracts::class)
inline fun <R, T : Any> ResultWrapper<T>.fold(
    onSuccess: (value: T) -> R,
    onError: (errorResource: Int) -> R,
): R {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onError, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is ResultWrapper.Success -> onSuccess(this.data)
        is ResultWrapper.GenericError -> onError(ru.mobileprism.autoredemption.R.string.api_error_message)
        else -> onError(ru.mobileprism.autoredemption.R.string.interner_error)
    }
}

class AutoBotError(
    val messageResource: Int? = null,
    val error: com.apollographql.apollo3.api.Error? = null
) : Exception()
