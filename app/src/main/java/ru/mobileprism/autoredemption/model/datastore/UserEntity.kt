package ru.mobileprism.autoredemption.model.datastore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime
import java.time.ZonedDateTime


@Parcelize
data class UserEntity(
    val _id: String = "0",
    val phone: String = "+71234567890",
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val subscriptionStatus: SubscriptionStatus = SubscriptionStatus(false, OffsetDateTime.now()),
    val role: String? = null,
    val city: CityEntity? = null,
    val timeZone: TimeZoneEntity? = null,
    val monthlyPayment: Int? = null,
    val name: String? = null,
) : Parcelable

@Parcelize
class TimeZoneEntity(
    val _id: String = "0",
    val label: String,
    val name: String,
    val utc: String,
    val msk: String,
) : Parcelable

@Parcelize
data class CityEntity(
    val _id: String = "0",
    val name: String,
    val timeZone: String,
) : Parcelable

@Parcelize
data class SubscriptionStatus(
    val isActive: Boolean,
    val subscriptionEnds: OffsetDateTime,
) : Parcelable
