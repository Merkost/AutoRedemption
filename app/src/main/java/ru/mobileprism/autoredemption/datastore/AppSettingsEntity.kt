package ru.mobileprism.autoredemption.datastore

data class AppSettingsEntity(
    val debugMode: Boolean = true,
    val messagesDelay: Long = 500,
    val timeInText: Boolean = true,

)
