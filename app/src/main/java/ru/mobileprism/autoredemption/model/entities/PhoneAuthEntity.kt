package ru.mobileprism.autoredemption.model.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhoneAuthEntity(
    var password: String,
    val phone: String,
    val isRegistered: Boolean = false,
) : Parcelable
