package ru.mobileprism.autoredemption.compose.screens

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.mobileprism.autoredemption.compose.MainDestinations
import ru.mobileprism.autoredemption.compose.screens.auth.PhoneEnteringScreen
import ru.mobileprism.autoredemption.compose.screens.auth.SmsConfirmScreen
import ru.mobileprism.autoredemption.model.entities.PhoneAuthEntity
import ru.mobileprism.autoredemption.utils.navigate
import ru.mobileprism.autoredemption.utils.requiredArg


object LoginDestinations {
    val PHONE_ENTERING_ROUTE = "PHONE_ENTERING_ROUTE"
    val SMS_CONFIRM_ROUTE = "SMS_CONFIRM_ROUTE"

}

object LoginArguments {
    const val PHONE_AUTH = "phone_auth_arg"
}


fun NavGraphBuilder.addAuthGraph(
    navController: NavController,
    upPress: () -> Unit
) {
    composable(LoginDestinations.PHONE_ENTERING_ROUTE) {
        PhoneEnteringScreen {
            navController.navigate(LoginDestinations.SMS_CONFIRM_ROUTE, LoginArguments.PHONE_AUTH to it)
        }
    }

    composable(LoginDestinations.SMS_CONFIRM_ROUTE) {
        val phoneAuthEntity = it.requiredArg<PhoneAuthEntity>(LoginArguments.PHONE_AUTH)
        SmsConfirmScreen(phoneAuthEntity, upPress = upPress) {
            navController.navigate(MainDestinations.HOME) {
                popUpTo(MainDestinations.AUTH_ROUTE) {
                    inclusive = true
                }
            }
        }
    }
}