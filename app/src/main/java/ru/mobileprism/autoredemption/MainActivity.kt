package ru.mobileprism.autoredemption

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ru.mobileprism.autoredemption.compose.AutoBotApp
import ru.mobileprism.autoredemption.compose.MainDestinations
import ru.mobileprism.autoredemption.compose.screens.*
import ru.mobileprism.autoredemption.compose.screens.auth.AuthState
import ru.mobileprism.autoredemption.compose.screens.home.MainScreen
import ru.mobileprism.autoredemption.ui.theme.AutoBotTheme
import ru.mobileprism.autoredemption.utils.checkNotificationPolicyAccess
import ru.mobileprism.autoredemption.utils.getSmsManager
import ru.mobileprism.autoredemption.utils.showToast


class MainActivity : ComponentActivity() {


    companion object {
        const val splashFadeDurationMillis = 300
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val smsManager = this.getSmsManager()
        val telephonyManager = this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        /*telephonyManager.createForSubscriptionId(1)*/

        /*val localSubscriptionManager = SubscriptionManager.from(this)
        if (localSubscriptionManager.activeSubscriptionInfoCount > 1) {
            val localList: List<*> = localSubscriptionManager.activeSubscriptionInfoList
            val simInfo = localList[simID] as SubscriptionInfo
            SmsManager.getSmsManagerForSubscriptionId(simInfo.subscriptionId)
                .sendTextMessage(toNum, null, smsText, null, null)
        }

        val subscriptionManager = SubscriptionManager.from(this)
        Log.d("abc", smsManager.subscriptionId.toString())
        smsManager.createForSubscriptionId()

        Log.d("abc", subscriptionManager.activeSubscriptionInfoList.toString())*/


        val viewModel: MainActivityViewModel = getViewModel()
        var authState: AuthState? = null

        lifecycleScope.launchWhenCreated {
            viewModel.authState.collect { authState = it }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                authState == null
            }
            setOnExitAnimationListener { splashScreenViewProvider ->
                // Get icon instance and start a fade out animation

                splashScreenViewProvider.view
                    .animate()
                    .setDuration(splashFadeDurationMillis.toLong())
                    .alpha(0f)
                    .start()


                splashScreenViewProvider.iconView
                    .animate()
                    .setDuration(splashFadeDurationMillis.toLong())
                    .alpha(0f)
                    /*.scaleX(50f)
                    .scaleY(50f)*/
                    /*.withEndAction {
                        splashScreenViewProvider.remove()
                        *//*setContent {
                            AutoRedemptionTheme {
                                Distribution(viewModel.authState.collectAsState().value)
                            }
                        }*//*
                    }*/
                    .start()
            }
        }

        setTheme(R.style.Theme_AnimatedSplashScreen)
        setContent {
            AutoBotTheme {
                Distribution(viewModel.authState.collectAsState().value)
            }
        }


    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun Distribution(authState: AuthState?) {
        when (authState) {
            AuthState.Logged -> {
                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                checkNotificationPolicyAccess(notificationManager, this)

                val simsPermissions =
                    rememberPermissionState(permission = Manifest.permission.READ_PHONE_STATE)
                SideEffect {
                    simsPermissions.launchPermissionRequest()
                }
                val context = LocalContext.current

                val requiredPermissions = rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.READ_PHONE_STATE,
                    )
                )

                if (notificationManager.areNotificationsEnabled()
                        .not() || requiredPermissions.allPermissionsGranted.not()
                ) {
                    PermissionsScreen()
                } else {
                    AutoBotApp()
                }
//                AutoBotApp()
            }
            AuthState.NotLogged, null -> {
                AutoBotApp(startRoute = MainDestinations.AUTH_ROUTE)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen() {
    val context = LocalContext.current
    val notificationManager: NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    val simsPermissions = rememberPermissionState(permission = Manifest.permission.READ_PHONE_STATE)


    val requiredPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_PHONE_STATE,
        )
    )


    Scaffold(modifier = Modifier.systemBarsPadding()) {
        DefaultColumn(modifier = Modifier.padding(30.dp)) {
            ActionCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(50.dp), onClick = {
                    if (simsPermissions.status.shouldShowRationale) {
                        context.showToast("Необходимо выдать все разрешения в настройках")
                        try {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", context.packageName, null)
                            intent.data = uri
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // TODO:
                            Log.w("TAG", e.message ?: "")
                        }
                    } else {
                        simsPermissions.launchPermissionRequest()
                    }
                }, text = "Разрешение на просмотр активных сим карт на устройстве",
                isPermissionGranted = simsPermissions.status.isGranted
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(
    modifier: Modifier,
    text: String,
    onClick: () -> Unit,
    isPermissionGranted: Boolean
) {
    Card(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, modifier = Modifier.weight(1f, false))
            Icon(
                if (isPermissionGranted) {
                    Icons.Default.Check
                } else {
                    Icons.Default.Circle
                },
                "",
                modifier = Modifier.padding(4.dp)
            )


        }
    }
}

@Composable
fun DefaultColumn(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(12.dp),
    function: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {
        function()
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AutoBotTheme {
        MainScreen({}) {}
    }
}

/**
 * A composable function that returns the [Resources]. It will be recomposed when `Configuration`
 * gets updated.
 */
@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}
