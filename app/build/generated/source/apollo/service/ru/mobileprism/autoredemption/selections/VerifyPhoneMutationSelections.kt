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
import ru.mobileprism.autoredemption.type.GraphQLBoolean
import ru.mobileprism.autoredemption.type.GraphQLString
import ru.mobileprism.autoredemption.type.VerifyPhoneResponse

public object VerifyPhoneMutationSelections {
  private val __verifyPhone: List<CompiledSelection> = listOf(
        CompiledField.Builder(
          name = "success",
          type = GraphQLBoolean.type.notNull()
        ).build(),
        CompiledField.Builder(
          name = "message",
          type = GraphQLString.type.notNull()
        ).build()
      )

  public val __root: List<CompiledSelection> = listOf(
        CompiledField.Builder(
          name = "verifyPhone",
          type = VerifyPhoneResponse.type
        ).arguments(listOf(
          CompiledArgument.Builder("phone", CompiledVariable("phone")).build()
        ))
        .selections(__verifyPhone)
        .build()
      )
}
