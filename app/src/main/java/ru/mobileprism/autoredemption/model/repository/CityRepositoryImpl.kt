package ru.mobileprism.autoredemption.model.repository

import com.apollographql.apollo3.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.GetCitiesAndTimezonesQuery
import ru.mobileprism.autoredemption.UpdateCityMutation
import ru.mobileprism.autoredemption.VerifyPhoneMutation


class CityRepositoryImpl(private val apolloClient: ApolloClient) : CityRepository {

    override suspend fun getCitiesAndTimezones() = flow {
        val result = safeGraphQLCall {
            apolloClient.query(GetCitiesAndTimezonesQuery()).execute()
        }
        emit(result)
    }

    override suspend fun updateCityAndTimezone(
        cityId: String,
        timezoneId: String
    ) = flow<ResultWrapper<UpdateCityMutation.Data>> {
        /*val result = safeGraphQLCall {
            TODO:
            apolloClient.mutation(UpdateCityMutation()).execute()
        }
        emit(result)*/
    }


}