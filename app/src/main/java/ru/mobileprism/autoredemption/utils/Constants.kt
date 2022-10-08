package ru.mobileprism.autoredemption.utils

import ru.mobileprism.autoredemption.BuildConfig

object Constants {
    const val apiUrl: String = "https://api.github.com/graphql"
    const val DEFAULT_MESSAGES_DELAY: Long = 500
    const val PHONE_DEFAULT_VALUE = "+7"
    val phoneRegex = "^\\+79[0-9]{2}[0-9]{3}[0-9]{2}[0-9]{2}".toRegex()

    val isDebug: Boolean
        get() = BuildConfig.DEBUG
}