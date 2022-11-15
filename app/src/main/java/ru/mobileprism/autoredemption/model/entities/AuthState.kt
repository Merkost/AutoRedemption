package ru.mobileprism.autoredemption.model.entities

import ru.mobileprism.autoredemption.model.datastore.UserEntity

sealed class AuthState {
    class Logged(val user: UserEntity): AuthState()
    object NotLogged: AuthState()
}