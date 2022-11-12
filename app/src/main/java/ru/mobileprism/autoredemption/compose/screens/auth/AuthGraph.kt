package ru.mobileprism.autoredemption.compose.screens.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.mobileprism.autoredemption.compose.MainDestinations
import ru.mobileprism.autoredemption.model.datastore.UserEntity
import ru.mobileprism.autoredemption.model.entities.PhoneAuthEntity
import ru.mobileprism.autoredemption.model.entities.SmsConfirmEntity
import ru.mobileprism.autoredemption.utils.navigate
import ru.mobileprism.autoredemption.utils.requiredArg


object LoginDestinations {
    val PHONE_ENTERING_ROUTE = "PHONE_ENTERING_ROUTE"
    val SMS_CONFIRM_ROUTE = "SMS_CONFIRM_ROUTE"
    val CHOOSE_CITY: String = "CHOOSE_CITY_ROUTE"

}

object LoginArguments {
    const val CONFIRM_SMS = "confirm_sms_arg"
    const val PHONE_AUTH = "phone_auth_arg"
}


fun NavGraphBuilder.addAuthGraph(
    navController: NavController,
    upPress: () -> Unit
) {

//    authManager.saveUserWithToken(UserMapper.mapDbUser(smsResult.user), smsResult.token)

    composable(LoginDestinations.PHONE_ENTERING_ROUTE) {
        PhoneEnteringScreen {
            navController.navigate(LoginDestinations.SMS_CONFIRM_ROUTE, LoginArguments.PHONE_AUTH to it)
        }
    }

    composable(LoginDestinations.SMS_CONFIRM_ROUTE) {
        val phoneAuthEntity = it.requiredArg<PhoneAuthEntity>(LoginArguments.PHONE_AUTH)
        SmsConfirmScreen(phoneAuthEntity, upPress = upPress) {
            navController.navigate(LoginDestinations.CHOOSE_CITY, LoginArguments.CONFIRM_SMS to it)
        }
    }

    composable(LoginDestinations.CHOOSE_CITY) {
//        val confirmSmsEntity = it.requiredArg<SmsConfirmEntity>(LoginArguments.CONFIRM_SMS)
        val confirmSmsEntity = SmsConfirmEntity("", UserEntity())
        ChooseCityScreen(confirmSmsEntity, upPress = upPress) {
            navController.navigate(MainDestinations.HOME) {
                popUpTo(MainDestinations.AUTH_ROUTE) {
                    inclusive = true
                }
            }
        }
    }
}


