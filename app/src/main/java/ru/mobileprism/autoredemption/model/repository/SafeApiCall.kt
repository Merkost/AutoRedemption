package ru.mobileprism.autoredemption.model.repository

import android.os.Parcelable
import androidx.annotation.StringRes
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.api.json.BufferedSinkJsonWriter.Companion.string
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import ru.mobileprism.autoredemption.R
import java.io.IOException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Parcelize
data class ServerError(
    val code: Int,
    val message: String,
) : Parcelable

private val com.apollographql.apollo3.api.Error?.toServerErrorResponse: ServerError
    get() =
        ServerError(
            code = this?.extensions?.get("code").toString().toIntOrNull() ?: 501,
            message = this?.extensions?.get("message") as String?
                ?: this?.message
            ?: "Unknown error"
        )

suspend fun <T : Operation.Data> safeGraphQLCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> ApolloResponse<T>,
): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            val result = apiCall.invoke()
            when {
                result.hasErrors() -> ResultWrapper.GenericError(result.errors?.firstOrNull()?.toServerErrorResponse)
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
    data class GenericError(val data: ServerError?) :
        ResultWrapper<Nothing>()

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
                    messageResource = /*this.data?.code?.getErrorRes ?: */ru.mobileprism.autoredemption.R.string.api_error_message,
                    message = this.data?.message
                )
            )
        else -> onError(
            AutoBotError(
                ru.mobileprism.autoredemption.R.string.interner_error,
                message = "Internet error"
            )
        )
    }
}

open class AutoBotError(
    @StringRes
    val messageResource: Int = R.string.unknown_error,
    override val message: String? = null,
    val code: Int? = null,
) : Exception() {

    object EmptyResponseError : AutoBotError(R.string.empty_response)

}



