package ru.mobileprism.autoredemption.model.datastore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.OffsetDateTime


@Parcelize
data class UserEntity(
    val _id: String = "0",
    val phone: String = "+71234567890",
    val createdAt: String = LocalDateTime.now().toString(),
    val subscriptionStatus: SubscriptionStatus = SubscriptionStatus(false, OffsetDateTime.now()),
    val firstname: String? = null,
    val lastname: String? = null,
) : Parcelable

@Parcelize
data class SubscriptionStatus(
    val isActive: Boolean,
    val subscriptionEnds: OffsetDateTime,
) : Parcelable
