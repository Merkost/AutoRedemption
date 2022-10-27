package ru.mobileprism.autoredemption.compose.screens.home

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import ru.mobileprism.autoredemption.R
import ru.mobileprism.autoredemption.compose.MainDestinations
import ru.mobileprism.autoredemption.compose.screens.MainScreen
import ru.mobileprism.autoredemption.model.datastore.UserEntity
import ru.mobileprism.autoredemption.type.User

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
fun NavGraphBuilder.addHomeGraph(
    navController: NavController,
    modifier: Modifier = Modifier,
    upPress: () -> Unit,
) {
    composable(
        HomeSections.MAIN.route,
    ) { from ->
        MainScreen(upPress, toSettings = { navController.navigate(MainDestinations.SETTINGS) })
    }

    composable(
        HomeSections.PROFILE.route
    ) { from ->
        ProfileScreen(upPress)

    }

    composable(
        HomeSections.SCENARIOS.route
    ) { from ->
        ScenariosScreen(upPress)
    }
}

@Composable
fun ScenariosScreen(upPress: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val user = remember {
        mutableStateOf(UserEntity())
    }

    Scaffold {
    }

}



enum class HomeSections(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    MAIN(R.string.main, Icons.Outlined.Home, "home/main"),
    PROFILE(R.string.profile, Icons.Outlined.Person, "home/profile"),
    SCENARIOS(R.string.scenarios, Icons.Outlined.Menu, "home/scenarios")
}

@Composable
fun AutoBotBottomBar(
    tabs: Array<HomeSections>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
    //color: Color = MaterialTheme.colors.iconPrimary,
    //contentColor: Color = MaterialTheme.colors.iconInteractive
) {
    val currentSection = tabs.first { it.route == currentRoute }

    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        content = {
            BottomNavigation() {
                tabs.forEach { section ->
                    val selected = section == currentSection
                    BottomNavigationItem(
                        icon = {
                            Icon(section.icon, section.name /*tint = tint*/)
                        },
                        label = {
                            Text(
                                stringResource(section.title),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis, /*color = tint*/
                            )
                        },
                        selected = selected,
                        onClick = { navigateToRoute(section.route) },
                        alwaysShowLabel = true,
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun AutoBotBottomNavPreview() {
    MaterialTheme {
        AutoBotBottomBar(
            tabs = HomeSections.values(),
            currentRoute = "home/main",
            navigateToRoute = { }
        )
    }
}