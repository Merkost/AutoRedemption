package ru.mobileprism.autobot.utils

import android.util.Log
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

object PhoneNumberFormatter {
    private val phoneNumberUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    fun formatToCountryCodeAndNationalNumber(phoneNumberWithCountryCode: String): Pair<Int, Long>? =
        try {
            // Can pass a default country code as the second param if there is one or an empty string if not
            val number = phoneNumberUtil.parse(phoneNumberWithCountryCode, "ru")
            Pair(number.countryCode, number.nationalNumber)
        } catch (e: NumberParseException) {
            Log.e(e.toString(), "Error getting country code and national number")
            null
        }
}