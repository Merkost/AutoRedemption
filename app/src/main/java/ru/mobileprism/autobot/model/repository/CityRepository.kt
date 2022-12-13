package ru.mobileprism.autobot.model.repository

import kotlinx.coroutines.flow.Flow
import ru.mobileprism.autobot.GetCitiesQuery
import ru.mobileprism.autobot.UpdateUserMutation

interface CityRepository {
    suspend fun getCities(): Flow<ResultWrapper<GetCitiesQuery.Data>>
    suspend fun registerUser(name: String, cityId: String): Flow<ResultWrapper<UpdateUserMutation.Data>>
}