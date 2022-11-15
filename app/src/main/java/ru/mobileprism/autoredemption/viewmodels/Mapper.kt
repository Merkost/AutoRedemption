package ru.mobileprism.autoredemption.viewmodels

import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.model.datastore.CityEntity
import ru.mobileprism.autoredemption.model.datastore.SubscriptionStatus
import ru.mobileprism.autoredemption.model.datastore.TimeZoneEntity
import ru.mobileprism.autoredemption.model.datastore.UserEntity
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

val String.toOffsetDateTime: OffsetDateTime
    get() = OffsetDateTime.parse(this)

object Mapper {
    fun mapDbUser(user: ConfirmSmsMutation.User) =
        with(user) {
            // TODO: createdAt fix
            UserEntity(
                _id = _id,
                phone = phone,
                createdAt = (createdAt.toString().toLong()).toLocalDateTime(),
                subscriptionStatus = mapDbSubscriptionStatus(subscriptionStatus),
                role = role,
                city = city?.let { mapDbCity(it) },
                timeZone = timezone?.let { mapDbTimezone(it) },
                monthlyPayment = monthlyPayment,
                name = name,

                )
        }

    private fun mapDbTimezone(timezone: ConfirmSmsMutation.Timezone): TimeZoneEntity =
        with(timezone) {
            TimeZoneEntity(
                _id = _id,
                label = label,
                name = name,
                utc = utc,
                msk = msk,
            )
        }

    private fun mapDbCity(city: ConfirmSmsMutation.City) = with(city) {
        CityEntity(
            _id = _id,
            label = label,
            timeZone = timezone,
        )
    }

    /*private fun mapCities(cities: Ci) {

    }*/

    private fun mapDbSubscriptionStatus(
        subscriptionStatus: ConfirmSmsMutation.SubscriptionStatus
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
