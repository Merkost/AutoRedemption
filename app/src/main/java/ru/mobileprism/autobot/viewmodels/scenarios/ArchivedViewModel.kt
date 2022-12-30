package ru.mobileprism.autobot.viewmodels.scenarios

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.mobileprism.autobot.compose.screens.home.scenarios.defaultArchiveModel

class ArchivedViewModel : ViewModel() {

    val values = MutableStateFlow(defaultArchiveModel)

    fun onHelloTextChanged(helloText: String) {
        values.update { it.copy(helloText = helloText) }
    }

    fun onPrimaryTextChanged(text: String) {
        values.update { it.copy(text = text) }
    }

    fun afterMinutesChanged(days: Int) {
        values.update { it.copy(daysAfter = days) }
    }
}