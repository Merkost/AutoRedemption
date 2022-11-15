package ru.mobileprism.autoredemption.utils

import androidx.annotation.StringRes
import ru.mobileprism.autoredemption.model.repository.AutoBotError

sealed class BaseViewState<out T> {
    data class Success<out T>(val data: T, val isLocal: Boolean = false) : BaseViewState<T>()
    data class Error(val autoBotError: AutoBotError) : BaseViewState<Nothing>()

    class Loading(val progress: Int? = null) : BaseViewState<Nothing>()
}