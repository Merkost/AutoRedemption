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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import ru.mobileprism.autoredemption.screens.HomeScreen
import ru.mobileprism.autoredemption.screens.SettingsScreen
import ru.mobileprism.autoredemption.ui.theme.AutoRedemptionTheme
import ru.mobileprism.autoredemption.workmanager.SendSMSWorker
import java.time.Duration
import java.util.concurrent.TimeUnit

object MainDestinations {
    val SETTINGS: String = "SETTINGS"
    val HOME: String = "HOME"
}

class MainActivity : ComponentActivity() {
    @SuppressLint("BatteryLife")
    @OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
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

fun getSendSMSWork(numbers: List<String>): PeriodicWorkRequest =
    PeriodicWorkRequestBuilder<SendSMSWorker>(10, TimeUnit.MINUTES)
        .setInputData(
            Data.Builder()
                .putStringArray(SendSMSWorker.NUMBERS_ARG, numbers.toTypedArray())
                .build()
        )
        .setConstraints(
            Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()
        )
        .apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setBackoffCriteria(BackoffPolicy.EXPONENTIAL, Duration.ofSeconds(5))
            }
        }.build()


@Suppress("DEPRECATION") // Deprecated for third party Services.
fun <T> Context.isServiceRunning(service: Class<T>) =
    (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }