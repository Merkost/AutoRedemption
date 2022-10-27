package ru.mobileprism.autoredemption

import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import ru.mobileprism.autoredemption.utils.checkNotificationPolicyAccess
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import ru.mobileprism.autoredemption.compose.AutoBotApp
import kotlinx.coroutines.InternalCoroutinesApi
import ru.mobileprism.autoredemption.compose.screens.*
import ru.mobileprism.autoredemption.ui.theme.AutoRedemptionTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class,
        ExperimentalAnimationApi::class, ExperimentalMaterialApi::class,
        InternalCoroutinesApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        setContent {
            AutoRedemptionTheme {
                checkNotificationPolicyAccess(notificationManager, this)
                AutoBotApp()
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
