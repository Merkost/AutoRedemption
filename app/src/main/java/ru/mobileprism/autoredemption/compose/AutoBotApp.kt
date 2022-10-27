package ru.mobileprism.autoredemption.compose

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import ru.mobileprism.autoredemption.BuildConfig
import ru.mobileprism.autoredemption.compose.screens.*
import ru.mobileprism.autoredemption.compose.screens.home.AutoBotBottomBar
import ru.mobileprism.autoredemption.compose.screens.home.HomeSections
import ru.mobileprism.autoredemption.compose.screens.home.addHomeGraph
import ru.mobileprism.autoredemption.compose.screens.registration.addRegistrationGraph
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.utils.Constants

@ExperimentalComposeUiApi
@ExperimentalPermissionsApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
@Composable
fun AutoBotApp() {
    val appStateHolder = rememberAppStateHolder()



    val appSettings: AppSettings = get()
    val user = appSettings.getCurrentUserNullable.collectAsState(null)
    val startDestination = remember(user.value) {
        if (user.value == null) MainDestinations.LOGIN_ROUTE
        else MainDestinations.HOME

    }

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
            startDestination = startDestination/*if (Constants.isDebug) MainDestinations.HOME
                                else MainDestinations.LOGIN_ROUTE*/,
            Modifier.padding(innerPaddingModifier)
        ) {
            NavGraph(
                navController = appStateHolder.navController,
                upPress = appStateHolder::upPress,
            )
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalComposeUiApi
@ExperimentalPermissionsApi
@ExperimentalAnimationApi
@InternalCoroutinesApi
@ExperimentalMaterialApi
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
        route = MainDestinations.LOGIN_ROUTE,
        startDestination = LoginDestinations.PHONE_ENTERING_ROUTE,
    ) {
        addAuthGraph(navController, upPress = upPress)
    }

    navigation(
        route = MainDestinations.REGISTRATION_ROUTE,
        startDestination = LoginDestinations.PHONE_ENTERING_ROUTE,
    ) {
        addRegistrationGraph(navController, upPress = upPress)
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


