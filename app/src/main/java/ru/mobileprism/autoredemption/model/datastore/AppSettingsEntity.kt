package ru.mobileprism.autoredemption.model.datastore

data class AppSettingsEntity(
    val testMode: Boolean = true,
    val numbers: List<String> = listOf(),
    val testNumbers: Set<String> = setOf("0"),
    val messagesDelay: Long = 500,
    val messageText: String = "SMS",
    val timeInText: Boolean = true,
    )
