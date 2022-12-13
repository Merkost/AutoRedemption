package ru.mobileprism.autobot.model.datastore

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime


@Parcelize
data class UserEntity(
    val _id: String = "0",
    val name: String? = null,
    val phone: String = "+71234567890",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val subscriptionStatus: SubscriptionStatus = SubscriptionStatus(false, LocalDateTime.now()),
    val role: UserRole? = null,
    val city: CityEntity? = null,
    val timeZone: TimeZoneEntity? = null,
    val monthlyPayment: Int? = null,
) : Parcelable {

    @IgnoredOnParcel
    val shouldRegister: Boolean
        get() = city == null || name == null

}

enum class UserRole(val type: String) {
    ADMIN("admin"),
    USER("user");
}

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
    val label: String,
    val timeZone: String,
) : Parcelable

@Parcelize
data class SubscriptionStatus(
    val isActive: Boolean,
    val subscriptionEnds: LocalDateTime,
) : Parcelable


