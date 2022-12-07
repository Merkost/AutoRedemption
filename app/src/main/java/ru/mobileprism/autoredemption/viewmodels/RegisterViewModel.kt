package ru.mobileprism.autoredemption.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mobileprism.autoredemption.fragment.CityFragment
import ru.mobileprism.autoredemption.model.repository.AutoBotError
import ru.mobileprism.autoredemption.model.repository.CityRepository
import ru.mobileprism.autoredemption.model.repository.fold
import ru.mobileprism.autoredemption.utils.BaseViewState

class RegisterViewModel(
    private val cityRepository: CityRepository,
) : ViewModel() {

    private val _valuesState = MutableStateFlow<BaseViewState<Unit>>(BaseViewState.Loading())
    val valuesState = _valuesState.asStateFlow()

    private val _uiState = MutableStateFlow<BaseViewState<Unit>?>(null)
    val uiState = _uiState.asStateFlow()

    val chosenValues = MutableStateFlow<RegisterData>(RegisterData())

    val couldSaveValues = chosenValues.map {
        it.city != null && it.name.isNotBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _cities = MutableStateFlow<List<CityFragment>>(
        listOf(CityFragment("1", "2", "3"))
    )
    val cities = combine(_cities, chosenValues) { cities, chosenValues ->
        cities.sortedBy { it.label }.filter {
            it.label.lowercase().startsWith(chosenValues.cityText.lowercase())
        }/*.take(5)*/
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    init {
        getCities()
    }

    fun retry() {
        _valuesState.update { BaseViewState.Loading() }
        getCities()
    }

    private fun getCities() {
        viewModelScope.launch {
            cityRepository.getCities().single().fold(
                onSuccess = {
                    it.cities?.let {
                        _cities.value = it.filterNotNull().map { it.cityFragment }
                    }
                    _valuesState.update { BaseViewState.Success(Unit) }
                },
                onError = { error ->
                    _valuesState.update { BaseViewState.Error(error) }
                }
            )
        }
    }

    fun onNameSelected(name: String) {
        viewModelScope.launch {
            chosenValues.update { it.copy(name = name) }
        }
    }

    fun onCitySelected(city: CityFragment) {
        viewModelScope.launch {
            chosenValues.update { it.copy(city = city) }
        }
    }

    fun resetChosenCity() {
        viewModelScope.launch {
            chosenValues.update {
                it.copy(
                    city = null,
                    cityText = ""
                )
            }
        }
    }


    fun saveChosenValues() {
        viewModelScope.launch {
            _uiState.update { BaseViewState.Loading() }
            chosenValues.value.let {
                if (it.city?._id != null) {
                    cityRepository.registerUser(it.name, it.city._id).single()
                        .fold(
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
            chosenValues.update { it.copy(cityText = text) }
        }
    }

}

data class RegisterData(
    val name: String = "",

    val city: CityFragment? = null,
    val cityText: String = "",
)
