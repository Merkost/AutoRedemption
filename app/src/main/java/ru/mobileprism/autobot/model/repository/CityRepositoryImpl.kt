package ru.mobileprism.autobot.model.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.mobileprism.autobot.GetCitiesQuery
import ru.mobileprism.autobot.UpdateUserMutation
import ru.mobileprism.autobot.model.datastore.UserDatastore
import ru.mobileprism.autobot.type.UpdateUserInput


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