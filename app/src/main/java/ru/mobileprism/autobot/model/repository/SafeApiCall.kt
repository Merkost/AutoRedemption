package ru.mobileprism.autobot.model.repository

import androidx.annotation.StringRes
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.exception.ApolloNetworkException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.mobileprism.autobot.R
import ru.mobileprism.autobot.utils.ServerError
import ru.mobileprism.autobot.utils.ServerGenericError
import ru.mobileprism.autobot.utils.toServerErrorResponse
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
                result.hasErrors() -> ResultWrapper.GenericError(result.errors?.first().toServerErrorResponse())
                else -> ResultWrapper.Success(result.data!!)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException, is ApolloNetworkException -> ResultWrapper.NetworkError
                else -> ResultWrapper.GenericError(ServerGenericError)
            }
        }
    }
}

sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class GenericError(val data: ServerError) : ResultWrapper<Nothing>()
    object NetworkError : ResultWrapper<Nothing>()
}

@OptIn(ExperimentalContracts::class)
inline fun <R, T : Any> ResultWrapper<T>.fold(
    onSuccess: (value: T) -> R,
    onError: (error: AutoBotError) -> R,
): R {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onError, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is ResultWrapper.Success -> onSuccess(this.data)
        is ResultWrapper.GenericError ->
            onError(
                AutoBotError(
                    messageResource = this.data.messageResource,
                    message = this.data.message
                )
            )
        is ResultWrapper.NetworkError -> onError(
            AutoBotError(
                ru.mobileprism.autobot.R.string.interner_error,
                message = "Internet error"
            )
        )
    }
}

open class AutoBotError(
    @StringRes
    val messageResource: Int = R.string.unknown_error,
    val message: String? = null,
) {
    object EmptyResponseError : AutoBotError(R.string.empty_response)
}



