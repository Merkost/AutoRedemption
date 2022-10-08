package ru.mobileprism.autoredemption

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import ru.mobileprism.autoredemption.compose.screens.*
import ru.mobileprism.autoredemption.ui.theme.AutoRedemptionTheme

object MainDestinations {
    val SMS_CONFIRM = "SMS"
    val LOGIN: String = "LOGIN"
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

                NavHost(
                    navController = navController,
                    startDestination = MainDestinations.LOGIN
                ) {

                    composable(MainDestinations.LOGIN) {
                        LoginScreen {
                            navController.navigate(MainDestinations.SMS_CONFIRM)
                        }
                    }

                    composable(MainDestinations.SMS_CONFIRM) {
                        SmsConfirmScreen {
                            navController.navigate(MainDestinations.HOME) {
                                popUpTo(MainDestinations.LOGIN) {
                                    inclusive = true
                                }
                            }
                        }
                    }

                    //Phone entering
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
