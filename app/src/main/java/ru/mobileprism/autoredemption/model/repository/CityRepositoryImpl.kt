package ru.mobileprism.autoredemption.model.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import ru.mobileprism.autoredemption.GetCitiesAndTimezonesQuery
import ru.mobileprism.autoredemption.UpdateUserMutation
import ru.mobileprism.autoredemption.model.datastore.UserDatastore
import ru.mobileprism.autoredemption.type.UpdateUserInput
import ru.mobileprism.autoredemption.type.User
import ru.mobileprism.autoredemption.viewmodels.Mapper


class CityRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val userDatastore: UserDatastore
) : CityRepository {

    override suspend fun getCitiesAndTimezones() = flow {
        val result = safeGraphQLCall {
            apolloClient.query(GetCitiesAndTimezonesQuery()).execute()
        }
        emit(result)
    }

    override suspend fun updateCityAndTimezone(
        cityId: String,
        timezoneId: String
    ) = flow {
        val userId = userDatastore.getCurrentUser.first()._id

        val result = safeGraphQLCall {
            apolloClient.mutation(
                UpdateUserMutation(
                    userId,
                    UpdateUserInput(
                        cityId = Optional.present(cityId),
                        timezoneId = Optional.present(timezoneId)
                    )
                )
            ).execute()
        }
        if (result is ResultWrapper.Success) {
            result.data.updateUser?.user?.userFragment?.let {
                userDatastore.saveCurrentUser(it)
            }
        }
        emit(result)
    }


}