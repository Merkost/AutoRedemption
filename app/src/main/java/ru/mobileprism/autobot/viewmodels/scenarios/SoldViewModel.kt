package ru.mobileprism.autobot.viewmodels.scenarios

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.mobileprism.autobot.compose.screens.home.scenarios.defaultArchiveModel
import ru.mobileprism.autobot.compose.screens.home.scenarios.defaultPriceChangedModel
import ru.mobileprism.autobot.compose.screens.home.scenarios.defaultSoldModel

class SoldViewModel : ViewModel() {

    val values = MutableStateFlow(defaultSoldModel)

    fun onHelloTextChanged(helloText: String) {
        values.update { it.copy(helloText = helloText) }
    }

    fun onPrimaryTextChanged(text: String) {
        values.update { it.copy(text = text) }
    }

    fun afterDaysChanged(days: String) {
        days.toIntOrNull()?.let { daysInt ->
            values.update { it.copy(daysAfter = daysInt) }

        }
    }
}