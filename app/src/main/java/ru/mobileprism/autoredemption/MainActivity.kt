package ru.mobileprism.autoredemption

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.os.Build
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
import ru.mobileprism.autoredemption.screens.HomeScreen
import ru.mobileprism.autoredemption.screens.SettingsScreen
import ru.mobileprism.autoredemption.ui.theme.AutoRedemptionTheme
import ru.mobileprism.autoredemption.workmanager.SendSMSWorker
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject

object MainDestinations {
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
                    startDestination = MainDestinations.HOME
                ) {

                    //Phone entering
                    composable(MainDestinations.HOME) {
                        HomeScreen { navController.navigate(MainDestinations.SETTINGS) }
                    }

                    composable(MainDestinations.SETTINGS) {
                        SettingsScreen(upPress = navController::popBackStack)
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
