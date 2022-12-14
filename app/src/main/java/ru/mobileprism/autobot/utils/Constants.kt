package ru.mobileprism.autobot.utils

import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter

object Constants {

    //Update scheme
    /*
    * ./gradlew :app:downloadApolloSchema --endpoint='http://188.120.229.170:3000/graphql' --schema=app/src/main/graphql/ru/mobileprism/autoredemption/schema.graphqls
    *
    * generateApolloSources
    * */

    val superSmallPadding = 4.dp
    val smallPadding = 8.dp
    val defaultPadding = 16.dp
    val largePadding = 32.dp
    const val apiUrl: String = "http://188.120.229.170:4000/graphql"
    const val DEFAULT_MESSAGES_DELAY: Long = 500
    const val RETRY_DELAY: Long = 200

    val SMS_RESEND_AWAIT: Int = if (isDebug) 5 else 60
    const val PHONE_DEFAULT_VALUE = "+7"
    val phoneRegex = "^\\+79[0-9]{2}[0-9]{3}[0-9]{2}[0-9]{2}".toRegex()

    val isDebug: Boolean
        get() = true/*BuildConfig.DEBUG*/

    //Time
    val DAY_MONTH_YEAR_TIME = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm")
    val DAY_MONTH_YEAR = DateTimeFormatter.ofPattern("d MMMM yyyy")
    val DAY_MONTH_TIME = DateTimeFormatter.ofPattern("d MMMM, HH:mm")
    val DAY_MONTH = DateTimeFormatter.ofPattern("d MMMM")
    val TIME = DateTimeFormatter.ofPattern("HH:mm")
}