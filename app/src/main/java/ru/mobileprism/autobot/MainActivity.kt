package ru.mobileprism.autobot

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ru.mobileprism.autobot.compose.AutoBotApp
import org.koin.androidx.compose.get
import ru.mobileprism.autobot.compose.MainDestinations
import ru.mobileprism.autobot.compose.screens.*
import ru.mobileprism.autobot.compose.screens.auth.LoginDestinations
import ru.mobileprism.autobot.model.entities.AuthState
import ru.mobileprism.autobot.utils.checkNotificationPolicyAccess
import ru.mobileprism.autobot.model.datastore.AppSettings
import ru.mobileprism.autobot.ui.theme3.AutoBotTheme
import ru.mobileprism.autobot.utils.*


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

                if (authState.user.shouldRegister) {
                    AutoBotApp(startRoute = MainDestinations.AUTH_ROUTE, startAuthRoute = LoginDestinations.REGISTER)
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
