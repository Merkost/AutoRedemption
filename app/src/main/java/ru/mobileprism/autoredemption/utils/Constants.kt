package ru.mobileprism.autoredemption.utils

import ru.mobileprism.autoredemption.BuildConfig

object Constants {
    const val apiUrl: String = "http://188.120.229.170/graphql"
    const val DEFAULT_MESSAGES_DELAY: Long = 500

    const val SMS_RESEND_AWAIT: Int = 60
    const val PHONE_DEFAULT_VALUE = "+7"
    val phoneRegex = "^\\+79[0-9]{2}[0-9]{3}[0-9]{2}[0-9]{2}".toRegex()

    const val AUTH_TAG = "AUTHENTICATION"

    val isDebug: Boolean
        get() = BuildConfig.DEBUG
}