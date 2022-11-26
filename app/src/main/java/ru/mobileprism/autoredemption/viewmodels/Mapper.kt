package ru.mobileprism.autoredemption.viewmodels

import ru.mobileprism.autoredemption.fragment.CityFragment
import ru.mobileprism.autoredemption.fragment.SubscriptionStatusFragment
import ru.mobileprism.autoredemption.fragment.TimezoneFragment
import ru.mobileprism.autoredemption.fragment.UserFragment

import ru.mobileprism.autoredemption.model.datastore.*
import ru.mobileprism.autoredemption.type.User
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

val String.toOffsetDateTime: OffsetDateTime
    get() = OffsetDateTime.parse(this)

object Mapper {
    fun mapDbUser(user: UserFragment) =
        with(user) {
            UserEntity(
                _id = _id,
                phone = phone,
                createdAt = (createdAt.toString().toLong()).toLocalDateTime(),
                subscriptionStatus = mapDbSubscriptionStatus(subscriptionStatus.subscriptionStatusFragment),
                role = UserRole.values().find { it.type == role } ?: UserRole.USER,
                city = city?.let { mapDbCity(it.cityFragment) },
                timeZone = timezone?.let { mapDbTimezone(it.timezoneFragment) },
                monthlyPayment = monthlyPayment,
                name = name,
            )
        }

    private fun mapDbTimezone(timezone: TimezoneFragment): TimeZoneEntity =
        with(timezone) {
            TimeZoneEntity(
                _id = _id,
                label = label,
                name = name,
                utc = utc,
                msk = msk,
            )
        }

    private fun mapDbCity(city: CityFragment) = with(city) {
        CityEntity(
            _id = _id,
            label = label,
            timeZone = timezone,
        )
    }

    private fun mapDbSubscriptionStatus(
        subscriptionStatus: SubscriptionStatusFragment
    ) = with(subscriptionStatus) {
        SubscriptionStatus(
            isActive,
            (subscriptionEnds.toString().toLong()).toLocalDateTime()
        )
    }

}

private fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime();
}
