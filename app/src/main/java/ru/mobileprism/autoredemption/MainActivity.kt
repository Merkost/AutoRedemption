package ru.mobileprism.autoredemption

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import ru.mobileprism.autoredemption.compose.screens.*
import ru.mobileprism.autoredemption.ui.theme.AutoRedemptionTheme

object MainDestinations {
    val LOGIN_ROUTE: String = "LOGIN"
    val LOGS: String = "LOGS"
    val SETTINGS: String = "SETTINGS"
    val HOME: String = "HOME"
}


class MainActivity : ComponentActivity() {
    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoRedemptionTheme {
                val navController = rememberNavController()
                val upPress: () -> Unit = {
                    navController.popBackStack()
                }

                NavHost(
                    navController = navController,
                    startDestination = MainDestinations.LOGIN_ROUTE
                ) {

                    navigation(
                        route = MainDestinations.LOGIN_ROUTE,
                        startDestination = LoginDestinations.PHONE_ENTERING_ROUTE,
                    ) {
                        addLoginGraph(navController, upPress = upPress)
                    }

                    composable(MainDestinations.HOME) {
                        HomeScreen { navController.navigate(MainDestinations.SETTINGS) }
                    }

                    composable(MainDestinations.SETTINGS) {
                        SettingsScreen(upPress = navController::popBackStack,
                            toLogs = {
                                navController.navigate(MainDestinations.LOGS)
                            })
                    }

                    composable(MainDestinations.LOGS) {
                        LogsScreen(navController::popBackStack)
                    }

                }
            }
        }
    }




}





@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AutoRedemptionTheme {
        HomeScreen {}
    }
}
