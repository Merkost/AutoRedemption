package ru.mobileprism.autoredemption.compose.screens.registration

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.mobileprism.autoredemption.compose.screens.LoginArguments
import ru.mobileprism.autoredemption.model.entities.PhoneAuthEntity
import ru.mobileprism.autoredemption.utils.requiredArg


object RegistrationDestinations {
    val SET_USER_INFO: String = "SET_USER_INFO_ROUTE"

}

object RegistrationArguments {

}


fun NavGraphBuilder.addRegistrationGraph(
    navController: NavController,
    upPress: () -> Unit
) {

    composable(RegistrationDestinations.SET_USER_INFO) {
        val phoneAuthEntity = it.requiredArg<PhoneAuthEntity>(LoginArguments.PHONE_AUTH)
//        Screen(phoneAuthEntity) {
//            navController.navigate(MainDestinations.HOME) {
//                popUpTo(MainDestinations.LOGIN_ROUTE) {
//                    inclusive = true
//                }
//            }
//        }
    }
}