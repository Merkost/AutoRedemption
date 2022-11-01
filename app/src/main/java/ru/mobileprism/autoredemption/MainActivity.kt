package ru.mobileprism.autoredemption

import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.StateFlow
import ru.mobileprism.autoredemption.utils.checkNotificationPolicyAccess
import ru.mobileprism.autoredemption.compose.AutoBotApp
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ru.mobileprism.autoredemption.compose.MainDestinations
import ru.mobileprism.autoredemption.compose.screens.*
import ru.mobileprism.autoredemption.compose.screens.auth.AuthState
import ru.mobileprism.autoredemption.ui.theme.AutoRedemptionTheme
import ru.mobileprism.autoredemption.utils.BaseViewState


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
                AutoRedemptionTheme {
                    Distribution(viewModel.authState.collectAsState().value)
                }
            }


    }

    @Composable
    fun Distribution(authState: AuthState?) {
        when (authState) {
            AuthState.Logged -> {
                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                checkNotificationPolicyAccess(notificationManager, this)
                AutoBotApp()
            }
            AuthState.NotLogged, null -> {
                AutoBotApp(startRoute = MainDestinations.AUTH_ROUTE)
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AutoRedemptionTheme {
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
