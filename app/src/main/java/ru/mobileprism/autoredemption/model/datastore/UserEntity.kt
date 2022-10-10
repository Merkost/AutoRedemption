package ru.mobileprism.autoredemption.model.datastore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserEntity(
    val _id: String,
    val phone: String,
    val createdAt: String,
    val subscriptionStatus: SubscriptionStatus,
    val firstname: String?,
    val lastname: String?,
) : Parcelable

@Parcelize
data class SubscriptionStatus(
    val isActive: Boolean,
    val subscriptionEnds: String,
) : Parcelable
