package ru.mobileprism.autoredemption.model.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.mobileprism.autoredemption.model.datastore.UserEntity

@Parcelize
data class SmsConfirmEntity(
    val token: String,
    val user: UserEntity,
) : Parcelable
