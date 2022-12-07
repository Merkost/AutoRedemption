package ru.mobileprism.autoredemption.model.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import ru.mobileprism.autoredemption.GetCitiesQuery
import ru.mobileprism.autoredemption.UpdateUserMutation
import ru.mobileprism.autoredemption.model.datastore.UserDatastore
import ru.mobileprism.autoredemption.type.UpdateUserInput
import ru.mobileprism.autoredemption.type.User
import ru.mobileprism.autoredemption.viewmodels.Mapper


class CityRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val userDatastore: UserDatastore
) : CityRepository {

    override suspend fun getCities() = flow {
        val result = safeGraphQLCall {
            apolloClient.query(GetCitiesQuery()).execute()
        }
        emit(result)
    }

    override suspend fun registerUser(
        name: String,
        cityId: String
    ) = flow {
        val userId = userDatastore.getCurrentUser.first()._id

        val result = safeGraphQLCall {
            apolloClient.mutation(
                UpdateUserMutation(
                    userId,
                    UpdateUserInput(
                        name = Optional.present(name),
                        cityId = Optional.present(cityId),
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