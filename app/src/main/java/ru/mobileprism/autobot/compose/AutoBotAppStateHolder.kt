package ru.mobileprism.autobot.compose

import android.content.res.Resources
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.navigation.*
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import ru.mobileprism.autobot.compose.screens.home.HomeSections
import ru.mobileprism.autobot.resources

/**
 * Destinations used in the [FishingNotesApp].
 */
object MainDestinations {
    const val HOME = "home"
    const val AUTH_ROUTE: String = "LOGIN"
    const val LOGS: String = "LOGS"
    const val SETTINGS: String = "SETTINGS"
    const val PERMISSIONS: String = "PERMISSIONS"
}

object ScenariosDestinations {
    const val ARCHIVED = "archived"
    const val SOLD = "sold"
    const val PRICE_CHANGED = "price_changed"
    const val PAID_AD = "paid_ad"
    const val OTHER_MESSAGES = "other_messages"
    const val FIRST_MESSAGE = "first_message"
}

object Arguments {

}

/**
 * Remembers and creates an instance of [AppStateHolder]
 */
@Composable
fun rememberAppStateHolder(
    navController: NavHostController = rememberNavController(),
//    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    remember(navController, /*snackbarManager, */resources, coroutineScope) {
        AppStateHolder(navController, /*snackbarManager, */resources, coroutineScope)
    }

/**
 * Responsible for holding state related to [FishingNotesApp] and containing UI-related logic.
 */
@Stable
class AppStateHolder(
//    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
//    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {

    // Process snackbars coming from SnackbarManager
    /*init {
        coroutineScope.launch {
            snackbarManager.messages.collect { currentMessages ->
                if (currentMessages.isNotEmpty()) {
                    val message = currentMessages[0]
                    val text = resources.getText(message.messageId)
                    val snackbarAction = message.snackbarAction

                    // Display the snackbar on the screen. `showSnackbar` is a function
                    // that suspends until the snackbar disappears from the screen
                    snackbarAction?.let {
                        val actionText = resources.getText(snackbarAction.textId)
                        val result = scaffoldState.snackbarHostState.showSnackbar(
                            message = text.toString(),
                            actionLabel = actionText.toString().uppercase(),
                            duration = message.duration
                        )
                        when (result) {
                            SnackbarResult.ActionPerformed -> snackbarAction.action
                            SnackbarResult.Dismissed -> {}
                        }
                    } ?: run {
                        scaffoldState.snackbarHostState.showSnackbar(text.toString())
                    }

                    // Once the snackbar is gone or dismissed, notify the SnackbarManager
                    snackbarManager.setMessageShown(message.id)
                }
            }
        }
    }*/

    // ----------------------------------------------------------
    // BottomBar state source of truth
    // ----------------------------------------------------------

    val bottomBarTabs = HomeSections.values()
    private val bottomBarRoutes = bottomBarTabs.map { it.route }

    // Reading this attribute will cause recompositions when the bottom bar needs shown, or not.
    // Not all routes need to show the bottom bar.
    val shouldShowBottomBar: Boolean
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination?.route in bottomBarRoutes

    // ----------------------------------------------------------
    // Navigation state source of truth
    // ----------------------------------------------------------

    val currentRoute: String?
        get() = navController.currentDestination?.route

    fun upPress() {
        navController.navigateUp()
    }

    fun navigateToBottomBarRoute(route: String) {
        if (route != currentRoute) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                // Pop up backstack to the first destination and save state. This makes going back
                // to the start destination when pressing back in any other bottom tab.
                popUpTo(findStartDestination(navController.graph).id) {
                    saveState = true
                }
            }
        }
    }
}

fun NavController.navigate(route: String, vararg args: Pair<String, Parcelable>) {
    navigate(route) {
        if (HomeSections.values().map { it.route }.contains(route)) {
            launchSingleTop = true
            restoreState = true
            // Pop up backstack to the first destination and save state. This makes going back
            // to the start destination when pressing back in any other bottom tab.
            popUpTo(findStartDestination(this@navigate.graph).id) {
                saveState = true
            }
        }
    }


    requireNotNull(currentBackStackEntry?.arguments).apply {
        args.forEach { (key: String, arg: Parcelable) ->
            putParcelable(key, arg)
        }
    }
}

inline fun <reified T : Parcelable> NavBackStackEntry.requiredArg(key: String): T {
    return requireNotNull(arguments) { "arguments bundle is null" }.run {
        requireNotNull(getParcelable(key)) { "argument for $key is null" }
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

/**
 * Copied from similar function in NavigationUI.kt
 *
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-ui/src/main/java/androidx/navigation/ui/NavigationUI.kt
 */
private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}