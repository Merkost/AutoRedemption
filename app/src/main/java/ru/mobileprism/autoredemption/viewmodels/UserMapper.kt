package ru.mobileprism.autoredemption.viewmodels

import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.model.datastore.CityEntity
import ru.mobileprism.autoredemption.model.datastore.SubscriptionStatus
import ru.mobileprism.autoredemption.model.datastore.TimeZoneEntity
import ru.mobileprism.autoredemption.model.datastore.UserEntity
import java.time.OffsetDateTime

val String.toOffsetDateTime: OffsetDateTime
    get() = OffsetDateTime.parse(this)

object UserMapper {
    fun mapDbUser(user: ConfirmSmsMutation.User) =
        with(user) {
            // TODO: createdAt fix
            UserEntity(
                _id = _id,
                phone = phone,
                createdAt = OffsetDateTime.now()/*OffsetDateTime.parse(createdAt)*/,
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
            name = name,
            timeZone = timezone,
        )
    }

    private fun mapDbSubscriptionStatus(
        subscriptionStatus: ConfirmSmsMutation.SubscriptionStatus
    ) = with(subscriptionStatus) {
        SubscriptionStatus(
            isActive,
            OffsetDateTime.now()/*OffsetDateTime.parse(subscriptionEnds)*/
        )
    }

}
