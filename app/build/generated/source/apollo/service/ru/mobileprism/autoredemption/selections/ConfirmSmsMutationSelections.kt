//
// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL version '3.6.0'.
//
package ru.mobileprism.autoredemption.selections

import com.apollographql.apollo3.api.CompiledArgument
import com.apollographql.apollo3.api.CompiledField
import com.apollographql.apollo3.api.CompiledSelection
import com.apollographql.apollo3.api.CompiledVariable
import com.apollographql.apollo3.api.notNull
import kotlin.collections.List
import ru.mobileprism.autoredemption.type.AuthResponse
import ru.mobileprism.autoredemption.type.City
import ru.mobileprism.autoredemption.type.GraphQLBoolean
import ru.mobileprism.autoredemption.type.GraphQLID
import ru.mobileprism.autoredemption.type.GraphQLInt
import ru.mobileprism.autoredemption.type.GraphQLString
import ru.mobileprism.autoredemption.type.SubscriptionStatus
import ru.mobileprism.autoredemption.type.Timezone
import ru.mobileprism.autoredemption.type.User

public object ConfirmSmsMutationSelections {
  private val __subscriptionStatus: List<CompiledSelection> = listOf(
        CompiledField.Builder(
          name = "isActive",
          type = GraphQLBoolean.type.notNull()
        ).build(),
        CompiledField.Builder(
          name = "subscriptionEnds",
          type = GraphQLString.type.notNull()
        ).build()
      )

  private val __city: List<CompiledSelection> = listOf(
        CompiledField.Builder(
          name = "_id",
          type = GraphQLString.type.notNull()
        ).build(),
        CompiledField.Builder(
          name = "name",
          type = GraphQLString.type.notNull()
        ).build(),
        CompiledField.Builder(
          name = "timezone",
          type = GraphQLString.type.notNull()
        ).build()
      )

  private val __timezone1: List<CompiledSelection> = listOf(
        CompiledField.Builder(
          name = "_id",
          type = GraphQLString.type.notNull()
        ).build(),
        CompiledField.Builder(
          name = "label",
          type = GraphQLString.type.notNull()
        ).build(),
        CompiledField.Builder(
          name = "name",
          type = GraphQLString.type.notNull()
        ).build(),
        CompiledField.Builder(
          name = "utc",
          type = GraphQLString.type.notNull()
        ).build(),
        CompiledField.Builder(
          name = "msk",
          type = GraphQLString.type.notNull()
        ).build()
      )

  private val __user: List<CompiledSelection> = listOf(
        CompiledField.Builder(
          name = "_id",
          type = GraphQLID.type.notNull()
        ).build(),
        CompiledField.Builder(
          name = "phone",
          type = GraphQLString.type.notNull()
        ).build(),
        CompiledField.Builder(
          name = "createdAt",
          type = GraphQLString.type.notNull()
        ).build(),
        CompiledField.Builder(
          name = "subscriptionStatus",
          type = SubscriptionStatus.type.notNull()
        ).selections(__subscriptionStatus)
        .build(),
        CompiledField.Builder(
          name = "role",
          type = GraphQLString.type.notNull()
        ).build(),
        CompiledField.Builder(
          name = "city",
          type = City.type
        ).selections(__city)
        .build(),
        CompiledField.Builder(
          name = "timezone",
          type = Timezone.type
        ).selections(__timezone1)
        .build(),
        CompiledField.Builder(
          name = "monthlyPayment",
          type = GraphQLInt.type
        ).build(),
        CompiledField.Builder(
          name = "name",
          type = GraphQLString.type
        ).build(),
        CompiledField.Builder(
          name = "comment",
          type = GraphQLString.type
        ).build()
      )

  private val __confirmSms: List<CompiledSelection> = listOf(
        CompiledField.Builder(
          name = "token",
          type = GraphQLString.type.notNull()
        ).build(),
        CompiledField.Builder(
          name = "user",
          type = User.type.notNull()
        ).selections(__user)
        .build()
      )

  public val __root: List<CompiledSelection> = listOf(
        CompiledField.Builder(
          name = "confirmSms",
          type = AuthResponse.type
        ).arguments(listOf(
          CompiledArgument.Builder("code", CompiledVariable("code")).build(),
          CompiledArgument.Builder("phone", CompiledVariable("phone")).build()
        ))
        .selections(__confirmSms)
        .build()
      )
}
