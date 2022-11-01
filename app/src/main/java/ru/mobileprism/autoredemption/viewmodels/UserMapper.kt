package ru.mobileprism.autoredemption.viewmodels

import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.model.datastore.SubscriptionStatus
import ru.mobileprism.autoredemption.model.datastore.UserEntity
import java.time.OffsetDateTime
import java.time.ZonedDateTime

val String.toOffsetDateTime: OffsetDateTime
    get() = OffsetDateTime.parse(this)

object UserMapper {
    fun mapDbUser(user: ConfirmSmsMutation.User) =
        with(user) {
            // TODO: createdAt fix
            UserEntity(
                _id,
                phone,
                OffsetDateTime.now().toString()/*createdAt*/,
                mapDbSubscriptionStatus(subscriptionStatus),
                firstname,
                lastname,
                )
        }

    private fun mapDbSubscriptionStatus(
        subscriptionStatus: ConfirmSmsMutation.SubscriptionStatus
    ) = with(subscriptionStatus) {
        SubscriptionStatus(
            isActive,
            OffsetDateTime.now().toString()
            // TODO:  subscriptionEnds.toOffsetDateTime
        )
    }

}
