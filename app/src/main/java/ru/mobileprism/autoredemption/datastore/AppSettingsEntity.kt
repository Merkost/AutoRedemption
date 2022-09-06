package ru.mobileprism.autoredemption.datastore

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

data class AppSettingsEntity(
    val debugMode: Boolean = true,
    val numbers: List<String> = listOf(),
    val messagesDelay: Long = 500,
    val messageText: String = "SMS",
    val timeInText: Boolean = true,
    )
