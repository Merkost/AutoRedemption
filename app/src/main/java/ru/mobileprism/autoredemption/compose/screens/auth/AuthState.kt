package ru.mobileprism.autoredemption.compose.screens.auth

sealed class AuthState {
    object Logged: AuthState()
    object NotLogged: AuthState()
}