package ru.mobileprism.autoredemption.utils

import ru.mobileprism.autoredemption.BuildConfig
import java.time.format.DateTimeFormatter

object Constants {
    const val apiUrl: String = "http://188.120.229.170:3000/graphql"
    const val DEFAULT_MESSAGES_DELAY: Long = 500
    const val RETRY_DELAY: Long = 200

    const val SMS_RESEND_AWAIT: Int = 60
    const val PHONE_DEFAULT_VALUE = "+7"
    val phoneRegex = "^\\+79[0-9]{2}[0-9]{3}[0-9]{2}[0-9]{2}".toRegex()

    const val AUTH_TAG = "AUTHENTICATION"

    val isDebug: Boolean
        get() = true/*BuildConfig.DEBUG*/

    //Time
    val DAY_MONTH_YEAR_TIME = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm")
    val DAY_MONTH_YEAR = DateTimeFormatter.ofPattern("d MMMM yyyy")
    val DAY_MONTH_TIME = DateTimeFormatter.ofPattern("d MMMM, HH:mm")
    val DAY_MONTH = DateTimeFormatter.ofPattern("d MMMM")
    val TIME = DateTimeFormatter.ofPattern("HH:mm")
}