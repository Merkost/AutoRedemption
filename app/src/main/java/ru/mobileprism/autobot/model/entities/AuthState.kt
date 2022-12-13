package ru.mobileprism.autobot.model.entities

import ru.mobileprism.autobot.model.datastore.UserEntity

sealed class AuthState {
    class Logged(val user: UserEntity): AuthState()
    object NotLogged: AuthState()
}