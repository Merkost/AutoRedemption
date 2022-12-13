package ru.mobileprism.autobot.utils

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import  com.apollographql.apollo3.api.Error
import ru.mobileprism.autobot.R

@Parcelize
data class ServerError(
    val code: Int,
    val message: String,
    @StringRes val messageResource: Int,
) : Parcelable

val ServerGenericError = ServerError(
    501,
    "GenericError",
    R.string.api_error_message
)

fun Error?.toServerErrorResponse(): ServerError {
    val code = this?.extensions?.get("code").toString().toIntOrNull() ?: 501
    return ServerError(
        code = code,
        message = this?.extensions?.get("message")?.toString() ?: this?.message ?: "Unknown error",
        messageResource = getErrorRes(code = code)
    )
}

private fun getErrorRes(code: Int): Int {
    return when (code) {
        101 -> R.string.invalid_phone
        102 -> R.string.invalid_otp

        201 -> R.string.access_denied
        202 -> R.string.verification_attempts_exceeded
        203 -> R.string.verification_attempts_exceeded
        204 -> R.string.otp_expired

        301 -> R.string.otp_not_found
        302 -> R.string.user_not_found
        303 -> R.string.update_user_error
        304 -> R.string.phone_duplication
        305 -> R.string.user_creation_error
        306 -> R.string.user_deletion_error
        307 -> R.string.database_error

        401 -> R.string.unable_send_sms

        501 -> R.string.api_error_message

        else -> R.string.api_error_message

    }
}

