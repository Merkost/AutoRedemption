package ru.mobileprism.autobot.viewmodels.scenarios

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.mobileprism.autobot.compose.screens.home.scenarios.defaultFirstMessageModel

class FirstMessageViewModel : ViewModel() {

    val values = MutableStateFlow(defaultFirstMessageModel)

    fun onHelloTextChanged(helloText: String) {
        values.update { it.copy(helloText = helloText) }
    }

    fun onPrimaryTextChanged(text: String) {
        values.update { it.copy(text = text) }
    }

    fun onShouldUniteChanged(newBool: Boolean) {
        values.update { it.copy(shouldUnite = newBool) }
    }

    fun afterDaysChanged(minutes: String) {
        minutes.toIntOrNull()?.let { intMin ->
            values.update { it.copy(minutesAfter = intMin) }
        }
    }
}