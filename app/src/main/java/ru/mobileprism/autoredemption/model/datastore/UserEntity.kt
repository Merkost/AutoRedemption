package ru.mobileprism.autoredemption.model.datastore

data class UserEntity(
    val phoneNum: String?,
    val email: String?,
    val userType: UserType = UserType.FREE
)

enum class UserType {
    PAID,
    FREE,
    TRIAL
}
