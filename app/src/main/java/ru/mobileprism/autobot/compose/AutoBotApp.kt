package ru.mobileprism.autobot.compose

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import ru.mobileprism.autobot.compose.screens.*
import ru.mobileprism.autobot.compose.screens.auth.LoginDestinations
import ru.mobileprism.autobot.compose.screens.auth.addAuthGraph
import ru.mobileprism.autobot.compose.screens.home.AutoBotBottomBar
import ru.mobileprism.autobot.compose.screens.home.HomeSections
import ru.mobileprism.autobot.compose.screens.home.addHomeGraph
import ru.mobileprism.autobot.compose.screens.home.scenarios.ScenariosFirstMessage
import ru.mobileprism.autobot.compose.screens.home.scenarios.ScenariosOtherMessages
import ru.mobileprism.autobot.compose.screens.home.scenarios.ScenariosPaidAd
import ru.mobileprism.autobot.compose.screens.home.scenarios.ScenariosPriceChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoBotApp(
    startRoute: String = MainDestinations.HOME,
    startAuthRoute: String = LoginDestinations.PHONE_ENTERING_ROUTE
) {
    val appStateHolder = rememberAppStateHolder()

    /*var startRoute: String = MainDestinations.HOME
    if (authState.user.shouldChooseCity) {
        startRoute = LoginDestinations.CHOOSE_CITY
    }*/

    //CheckForPermissions(appStateHolder.navController)


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
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
//        scaffoldState = appStateHolder.scaffoldState,
        modifier = Modifier
    ) { innerPaddingModifier ->
        NavHost(
            navController = appStateHolder.navController,
            startDestination = startRoute,
            modifier = Modifier.padding(innerPaddingModifier)
        ) {
            NavGraph(
                navController = appStateHolder.navController,
                upPress = appStateHolder::upPress,
                startAuthRoute = startAuthRoute,
            )
        }
    }

}

private fun NavGraphBuilder.NavGraph(
    upPress: () -> Unit,
    navController: NavController,
    startAuthRoute: String,
) {
    navigation(
        route = MainDestinations.HOME,
        startDestination = HomeSections.MAIN.route,
    ) {
        addHomeGraph(navController, upPress = upPress)
    }

    navigation(
        route = MainDestinations.AUTH_ROUTE,
        startDestination = startAuthRoute,
    ) {
        addAuthGraph(navController, upPress = upPress)
    }

    composable(MainDestinations.SETTINGS) {
        SettingsScreen(upPress = navController::popBackStack,
            toLogs = { navController.navigate(MainDestinations.LOGS) },
            toPermissions = { navController.navigate(MainDestinations.PERMISSIONS) }
        )
    }

    composable(MainDestinations.PERMISSIONS) {
        PermissionsScreen(upPress = upPress)
    }

    composable(MainDestinations.LOGS) {
        LogsScreen(navController::popBackStack)
    }

    composable(ScenariosDestinations.FIRST_MESSAGE) {
        ScenariosFirstMessage(upPress)
    }

    composable(ScenariosDestinations.OTHER_MESSAGES) {
        ScenariosOtherMessages(upPress)
    }

    composable(ScenariosDestinations.PAID_AD) {
        ScenariosPaidAd(upPress)
    }

    composable(ScenariosDestinations.PRICE_CHANGED) {
        ScenariosPriceChanged(upPress)
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckForPermissions(toPermissions: () -> Unit) {
    val context = LocalContext.current
    val notificationManager: NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    val requiredPermissions = rememberMultiplePermissionsState(
        listOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS)
    )

    DisposableEffect(Unit) {
        if (notificationManager.areNotificationsEnabled()
                .not() || requiredPermissions.allPermissionsGranted.not()
        ) { toPermissions() }
        onDispose {}
    }

}