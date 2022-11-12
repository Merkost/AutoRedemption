package ru.mobileprism.autoredemption.model.repository

import kotlinx.coroutines.flow.Flow
import ru.mobileprism.autoredemption.GetCitiesAndTimezonesQuery
import ru.mobileprism.autoredemption.UpdateCityMutation

interface CityRepository {
    suspend fun getCitiesAndTimezones(): Flow<ResultWrapper<GetCitiesAndTimezonesQuery.Data>>
    suspend fun updateCityAndTimezone(cityId: String, timezoneId: String): Flow<ResultWrapper<UpdateCityMutation.Data>>
}