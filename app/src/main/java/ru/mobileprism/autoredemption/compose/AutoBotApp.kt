package ru.mobileprism.autoredemption.compose

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import ru.mobileprism.autoredemption.compose.screens.*
import ru.mobileprism.autoredemption.compose.screens.auth.LoginDestinations
import ru.mobileprism.autoredemption.compose.screens.auth.addAuthGraph
import ru.mobileprism.autoredemption.compose.screens.home.AutoBotBottomBar
import ru.mobileprism.autoredemption.compose.screens.home.HomeSections
import ru.mobileprism.autoredemption.compose.screens.home.addHomeGraph

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AutoBotApp(startRoute: String = MainDestinations.HOME) {
    val appStateHolder = rememberAppStateHolder()

    /*var startRoute: String = MainDestinations.HOME
    if (authState.user.shouldChooseCity) {
        startRoute = LoginDestinations.CHOOSE_CITY
    }*/

    //CheckForPermissions()


//    val mainViewModel: MainViewModel = getViewModel()

    Scaffold(
        bottomBar = {
            if (appStateHolder.shouldShowBottomBar) {
                AutoBotBottomBar(
                    tabs = appStateHolder.bottomBarTabs,
                    currentRoute = appStateHolder.currentRoute!!,
                    navigateToRoute = appStateHolder::navigateToBottomBarRoute
                )
            }
        },
        snackbarHost = {
//            SnackbarHost(
//                hostState = it,
//                modifier = Modifier.systemBarsPadding(),
//                snackbar = { snackbarData -> AppSnackbar(snackbarData) }
//            )
        },
//        scaffoldState = appStateHolder.scaffoldState,
        modifier = Modifier
    ) { innerPaddingModifier ->
        NavHost(
            navController = appStateHolder.navController,
            startDestination = startRoute,
            Modifier.padding(innerPaddingModifier)
        ) {
            NavGraph(
                navController = appStateHolder.navController,
                upPress = appStateHolder::upPress,
            )
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CheckForPermissions() {
    val context = LocalContext.current
    val notificationManager: NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    val requiredPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_PHONE_STATE,
        )
    )

    if (notificationManager.areNotificationsEnabled().not() || requiredPermissions.allPermissionsGranted.not()) {
        Scaffold {
            Text("Permissions needed")
        }
    }

}

private fun NavGraphBuilder.NavGraph(
    upPress: () -> Unit,
    navController: NavController,
) {
    navigation(
        route = MainDestinations.HOME,
        startDestination = HomeSections.MAIN.route,
    ) {
        addHomeGraph(navController, upPress = upPress)
    }

    navigation(
        route = MainDestinations.AUTH_ROUTE,
        startDestination = LoginDestinations.PHONE_ENTERING_ROUTE,
    ) {
        addAuthGraph(navController, upPress = upPress)
    }

    composable(MainDestinations.SETTINGS) {
        SettingsScreen(upPress = navController::popBackStack,
            toLogs = { navController.navigate(MainDestinations.LOGS) }
        )
    }

    composable(MainDestinations.LOGS) {
        LogsScreen(navController::popBackStack)
    }

}


