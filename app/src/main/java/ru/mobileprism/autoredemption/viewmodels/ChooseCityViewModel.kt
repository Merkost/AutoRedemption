package ru.mobileprism.autoredemption.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication.Companion.init
import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.GetCitiesAndTimezonesQuery
import ru.mobileprism.autoredemption.R
import ru.mobileprism.autoredemption.fragment.CityFragment
import ru.mobileprism.autoredemption.fragment.TimezoneFragment
import ru.mobileprism.autoredemption.model.datastore.UserDatastore
import ru.mobileprism.autoredemption.model.repository.AutoBotError
import ru.mobileprism.autoredemption.model.repository.CityRepository
import ru.mobileprism.autoredemption.model.repository.fold
import ru.mobileprism.autoredemption.utils.BaseViewState

class ChooseCityViewModel(
    private val cityRepository: CityRepository,
) : ViewModel() {

    private val _valuesState = MutableStateFlow<BaseViewState<Unit>>(BaseViewState.Loading())
    val valuesState = _valuesState.asStateFlow()

    private val _uiState = MutableStateFlow<BaseViewState<Unit>?>(null)
    val uiState = _uiState.asStateFlow()

    val chosenCityAndTimezone = MutableStateFlow<CityAndTimezone>(CityAndTimezone())

    val couldSaveValues = chosenCityAndTimezone.map {
        it.city != null && it.timezone != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _cities = MutableStateFlow<List<CityFragment>>(
        listOf(CityFragment("1", "2", "3"))
    )
    val cities = combine(_cities, chosenCityAndTimezone) { cities, chosenValues ->
        cities.sortedBy { it.label }.filter { it.label.lowercase().startsWith(chosenValues.cityText.lowercase()) }/*.take(5)*/
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val _timezones = MutableStateFlow<List<TimezoneFragment>>(
        listOf(TimezoneFragment("1", "2", "3", "4", "5"))
    )
    val timezones = combine(_timezones, chosenCityAndTimezone) { cities, chosenValues ->
        cities.sortedBy { it.label }.filter { it.label.lowercase().startsWith(chosenValues.timezoneText.lowercase()) }/*.take(5)*/
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())


    init { getCities() }

    fun retry() {
        _valuesState.update { BaseViewState.Loading() }
        getCities()
    }

    private fun getCities() {
        viewModelScope.launch {
            cityRepository.getCitiesAndTimezones().single().fold(
                onSuccess = {
                    it.getCities?.let {
                        _cities.value = it.filterNotNull().map { it.cityFragment }
                    }
                    it.getTimezones?.let {
                        _timezones.value = it.filterNotNull().map { it.timezoneFragment }
                    }
                    _valuesState.update { BaseViewState.Success(Unit) }
                },
                onError = { error ->
                    _valuesState.update { BaseViewState.Error(error) }
                }
            )
        }
    }

    fun onCitySelected(city: CityFragment) {
        viewModelScope.launch {
            chosenCityAndTimezone.update { it.copy(city = city) }
        }
    }

    fun onTimezoneSelected(timezone: TimezoneFragment) {
        viewModelScope.launch {
            chosenCityAndTimezone.update { it.copy(timezone = timezone) }
        }
    }

    fun resetChosenCity() {
        viewModelScope.launch {
            chosenCityAndTimezone.update {
                it.copy(
                    city = null,
                    cityText = ""
                )
            }
        }
    }

    fun resetChosenTimezone() {
        viewModelScope.launch {
            chosenCityAndTimezone.update { it.copy(timezone = null, timezoneText = "") }
        }
    }

    fun saveChosenValues() {
        viewModelScope.launch {
            _uiState.update { BaseViewState.Loading() }
            chosenCityAndTimezone.value.let {
                if (it.city?._id != null && it.timezone?._id != null) {
                    cityRepository.updateCityAndTimezone(it.city._id, it.timezone._id).single().fold(
                        onSuccess = {
                            _uiState.update { BaseViewState.Success(Unit) }
                        },
                        onError = { error ->
                            _uiState.update { BaseViewState.Error(error) }
                        }
                    )
                } else {
                    _uiState.update { BaseViewState.Error(AutoBotError()) }
                }
            }
        }
    }

    fun onNewCityTextInput(text: String) {
        viewModelScope.launch {
            chosenCityAndTimezone.update { it.copy(cityText = text) }
        }
    }

    fun onNewTimezoneTextInput(text: String) {
        viewModelScope.launch {
            chosenCityAndTimezone.update { it.copy(timezoneText = text) }
        }
    }


}

data class CityAndTimezone(
    val city: CityFragment? = null,
    val cityText: String = "",

    val timezone: TimezoneFragment? = null,
    val timezoneText: String = "",
)
