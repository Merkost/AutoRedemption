package ru.mobileprism.autobot.viewmodels.scenarios

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.mobileprism.autobot.compose.screens.home.scenarios.defaultPriceChangedModel

class PriceChangedViewModel : ViewModel() {

    val values = MutableStateFlow(defaultPriceChangedModel)

    fun onHelloTextChanged(helloText: String) {
        values.update { it.copy(helloText = helloText) }
    }

    fun onPrimaryTextChanged(text: String) {
        values.update { it.copy(text = text) }
    }

    fun onePerDayChanged(bool: Boolean) {
        values.update { it.copy(onePerDay = bool) }
    }

    fun otherScenariosChanged(bool: Boolean) {
        values.update { it.copy(dontSendIfOthersActive = bool) }
    }

    fun afterMinutesChanged(minutes: Int) {
        values.update { it.copy(minutesAfter = minutes) }
    }
}