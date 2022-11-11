package ru.mobileprism.autoredemption.model

import com.apollographql.apollo3.exception.ApolloException

class ServerError(message: String?) : ApolloException(message)