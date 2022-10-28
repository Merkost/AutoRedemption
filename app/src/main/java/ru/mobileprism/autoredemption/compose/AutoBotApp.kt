package ru.mobileprism.autoredemption.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.koin.androidx.compose.get
import ru.mobileprism.autoredemption.compose.screens.*
import ru.mobileprism.autoredemption.compose.screens.home.AutoBotBottomBar
import ru.mobileprism.autoredemption.compose.screens.home.HomeSections
import ru.mobileprism.autoredemption.compose.screens.home.addHomeGraph
import ru.mobileprism.autoredemption.model.datastore.AppSettings

@Composable
fun AutoBotApp(startRoute: String = MainDestinations.HOME) {
    val appStateHolder = rememberAppStateHolder()

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
        scaffoldState = appStateHolder.scaffoldState,
        modifier = Modifier.systemBarsPadding()
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
            toLogs = {
                navController.navigate(MainDestinations.LOGS)
            })
    }

    composable(MainDestinations.LOGS) {
        LogsScreen(navController::popBackStack)
    }

}


