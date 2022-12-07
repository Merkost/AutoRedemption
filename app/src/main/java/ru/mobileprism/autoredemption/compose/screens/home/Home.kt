package ru.mobileprism.autoredemption.compose.screens.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.mobileprism.autoredemption.R
import ru.mobileprism.autoredemption.compose.MainDestinations
import ru.mobileprism.autoredemption.compose.screens.auth.LoginDestinations

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
        ProfileScreen(upPress, toAuth = {
            navController.navigate(LoginDestinations.PHONE_ENTERING_ROUTE) {
                popUpTo(0) { inclusive = true }
            }
        })
    }

    composable(
        HomeSections.SCENARIOS.route
    ) { from ->
        ScenariosScreen(upPress)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenariosScreen(upPress: () -> Unit) {

    Scaffold() {

    }

}


enum class HomeSections(
    @StringRes val title: Int, val icon: ImageVector, val route: String
) {
    MAIN(R.string.main, Icons.Outlined.Home, "home/main"), SCENARIOS(
        R.string.scenarios,
        Icons.Outlined.Menu,
        "home/scenarios"
    ),
    PROFILE(R.string.profile, Icons.Outlined.Person, "home/profile"),
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

    BottomAppBar(modifier = Modifier.fillMaxWidth(), content = {
        NavigationBar() {
            tabs.forEach { section ->
                val selected = section == currentSection
                NavigationBarItem(
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
    })
}

@Preview
@Composable
private fun AutoBotBottomNavPreview() {
    MaterialTheme {
        AutoBotBottomBar(tabs = HomeSections.values(),
            currentRoute = "home/main",
            navigateToRoute = { })
    }
}