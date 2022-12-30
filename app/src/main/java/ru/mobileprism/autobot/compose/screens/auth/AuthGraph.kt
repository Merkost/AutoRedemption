package ru.mobileprism.autobot.compose.screens.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.mobileprism.autobot.compose.MainDestinations
import ru.mobileprism.autobot.model.entities.PhoneAuthEntity
import ru.mobileprism.autobot.utils.navigate
import ru.mobileprism.autobot.utils.requiredArg


object LoginDestinations {
    const val PHONE_ENTERING_ROUTE = "PHONE_ENTERING_ROUTE"
    const val SMS_CONFIRM_ROUTE = "SMS_CONFIRM_ROUTE"
    const val REGISTER: String = "REGISTER_ROUTE"
}

object LoginArguments {
    const val PHONE_AUTH = "phone_auth_arg"
}


fun NavGraphBuilder.addAuthGraph(
    navController: NavController, upPress: () -> Unit
) {

    val navigateToApp: () -> Unit = {
        navController.navigate(MainDestinations.HOME) {
            popUpTo(MainDestinations.AUTH_ROUTE) {
                inclusive = true
            }
        }
    }

    composable(LoginDestinations.PHONE_ENTERING_ROUTE) {
        PhoneEnteringScreen {
            navController.navigate(
                LoginDestinations.SMS_CONFIRM_ROUTE, LoginArguments.PHONE_AUTH to it
            )
        }
    }

    composable(LoginDestinations.SMS_CONFIRM_ROUTE) {
        val phoneAuthEntity = it.requiredArg<PhoneAuthEntity>(LoginArguments.PHONE_AUTH)
        SmsConfirmScreen(phoneAuthEntity, upPress = upPress) {
            if (it.user.shouldRegister) {
                navController.navigate(LoginDestinations.REGISTER) {
                    popUpTo(MainDestinations.AUTH_ROUTE) {
                        inclusive = true
                    }
                }
            } else {
                navigateToApp()
            }
        }
    }

    composable(LoginDestinations.REGISTER) {
        RegisterScreen(upPress = upPress, onAuth = {
            navController.navigate(LoginDestinations.PHONE_ENTERING_ROUTE) {
                launchSingleTop = true
                popUpTo(0)
            }
        }) { navigateToApp() }
    }
}



