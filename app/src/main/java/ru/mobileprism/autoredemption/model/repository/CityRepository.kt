package ru.mobileprism.autoredemption.model.repository

import kotlinx.coroutines.flow.Flow
import ru.mobileprism.autoredemption.GetCitiesQuery
import ru.mobileprism.autoredemption.UpdateUserMutation

interface CityRepository {
    suspend fun getCities(): Flow<ResultWrapper<GetCitiesQuery.Data>>
    suspend fun registerUser(name: String, cityId: String): Flow<ResultWrapper<UpdateUserMutation.Data>>
}