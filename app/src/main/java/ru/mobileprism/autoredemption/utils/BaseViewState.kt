package ru.mobileprism.autoredemption.utils

import androidx.annotation.StringRes

sealed class BaseViewState<out T> {
    data class Success<out T>(val data: T, val isLocal: Boolean = false) : BaseViewState<T>()
    data class Error(
        val text: String? = null,
        val error: Throwable? = null,
        @StringRes val stringRes: Int? = null,
    ) : BaseViewState<Nothing>()

    class Loading(val progress: Int? = null) : BaseViewState<Nothing>()
}