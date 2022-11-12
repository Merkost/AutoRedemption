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
import ru.mobileprism.autoredemption.model.ServerError
import ru.mobileprism.autoredemption.model.datastore.CityEntity
import ru.mobileprism.autoredemption.model.datastore.TimeZoneEntity
import ru.mobileprism.autoredemption.model.entities.PhoneAuthEntity
import ru.mobileprism.autoredemption.model.entities.SmsConfirmEntity
import ru.mobileprism.autoredemption.model.repository.AuthRepository
import ru.mobileprism.autoredemption.model.repository.AutoBotError
import ru.mobileprism.autoredemption.model.repository.CityRepository
import ru.mobileprism.autoredemption.model.repository.fold
import ru.mobileprism.autoredemption.type.City
import ru.mobileprism.autoredemption.utils.BaseViewState
import ru.mobileprism.autoredemption.utils.Constants.RETRY_DELAY
import ru.mobileprism.autoredemption.utils.Constants.SMS_RESEND_AWAIT

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

    private val _cities = MutableStateFlow<List<GetCitiesAndTimezonesQuery.GetCity>>(listOf(
        GetCitiesAndTimezonesQuery.GetCity("1", "2","3",)
    ))
    val cities = _cities.asStateFlow()

    private val _timezones = MutableStateFlow<List<GetCitiesAndTimezonesQuery.GetTimezone>>(listOf(
        GetCitiesAndTimezonesQuery.GetTimezone("1","2","3","4","5")
    ))
    val timezones = _timezones.asStateFlow()


    init {
        getCities()
    }

    private fun getCities() {
        viewModelScope.launch {
            cityRepository.getCitiesAndTimezones().single().fold(
                onSuccess = {
                    it.getCities?.let {
                        _cities.value = it.filterNotNull()
                    }
                    it.getTimezones?.let {
                        _timezones.value = it.filterNotNull()
                    }
                    _uiState.update { BaseViewState.Success(Unit) }
                },
                onError = { errorRes ->
                    _uiState.update { BaseViewState.Error(stringRes = errorRes) }
                }
            )
        }
    }

    fun onCitySelected(city: GetCitiesAndTimezonesQuery.GetCity) {
        viewModelScope.launch {
            chosenCityAndTimezone.update { it.copy(city = city) }
        }
    }

    fun onTimezoneSelected(timezone: GetCitiesAndTimezonesQuery.GetTimezone) {
        viewModelScope.launch {
            chosenCityAndTimezone.update { it.copy(timezone = timezone) }
        }
    }

    fun resetChosenCity() {
        viewModelScope.launch {
            chosenCityAndTimezone.update { it.copy(city = null) }
        }
    }

    fun resetChosenTimezone() {
        viewModelScope.launch {
            chosenCityAndTimezone.update { it.copy(timezone = null) }
        }
    }

    fun saveChosenValues() {
        viewModelScope.launch {

        }
    }


}

data class CityAndTimezone(
    val city: GetCitiesAndTimezonesQuery.GetCity? = null,
    val timezone: GetCitiesAndTimezonesQuery.GetTimezone? = null
)
