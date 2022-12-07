package ru.mobileprism.autoredemption

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material.icons.outlined.SimCard
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
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.mobileprism.autoredemption.compose.CheckForPermissions
import ru.mobileprism.autoredemption.compose.MainDestinations
import ru.mobileprism.autoredemption.compose.screens.*
import ru.mobileprism.autoredemption.model.entities.AuthState
import ru.mobileprism.autoredemption.compose.screens.auth.LoginDestinations
import ru.mobileprism.autoredemption.compose.screens.home.MainScreen
import ru.mobileprism.autoredemption.utils.checkNotificationPolicyAccess
import ru.mobileprism.autoredemption.utils.showToast
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.ui.theme3.AutoBotTheme
import ru.mobileprism.autoredemption.utils.*


class MainActivity : ComponentActivity() {


    companion object {
        const val splashFadeDurationMillis = 300
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

                splashScreenViewProvider.view.animate()
                    .setDuration(splashFadeDurationMillis.toLong()).alpha(0f).start()


                /*splashScreenViewProvider.iconView
                    .animate()
                    .setDuration(splashFadeDurationMillis.toLong())
                    .alpha(0f)
                    .start()*/
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

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val appSettings: AppSettings = get()
        val chosenSimCardFromSettings = appSettings.selectedSimId.collectAsState(1)

        val requiredPermissions = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS
            )
        )

        when (authState) {
            is AuthState.Logged -> {
                checkNotificationPolicyAccess(notificationManager, this)

                if (authState.user.shouldChooseCity) {
                    AutoBotApp(startRoute = MainDestinations.AUTH_ROUTE)
                } else {
                    AutoBotApp(startRoute = MainDestinations.PERMISSIONS)
                }

            }
            AuthState.NotLogged, null -> {
                AutoBotApp(startRoute = MainDestinations.AUTH_ROUTE)
            }
        }
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
