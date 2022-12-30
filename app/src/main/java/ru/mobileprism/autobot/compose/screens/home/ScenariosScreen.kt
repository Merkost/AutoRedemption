package ru.mobileprism.autobot.compose.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.mobileprism.autobot.R
import ru.mobileprism.autobot.compose.ScenariosDestinations
import ru.mobileprism.autobot.compose.custom.DefaultColumn
import ru.mobileprism.autobot.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenariosScreen(upPress: () -> Unit, navController: NavController) {

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = stringResource(id = R.string.scenarios)) },)
    }) {
        DefaultColumn(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Constants.defaultPadding)
                .padding(it),
        ) {
            Text(text = "Сценарии позволяют настроить отправку сообщений на определенные события.")
            Column(
                verticalArrangement = Arrangement.spacedBy(Constants.smallPadding),
                modifier = Modifier.padding(top = Constants.smallPadding)
            ) {
                MenuButton(
                    title = stringResource(id = R.string.first_message),
                    icon = Icons.Default.Reply
                ) {
                    navController.navigate(ScenariosDestinations.FIRST_MESSAGE)
                }
                MenuButton(
                    title = stringResource(id = R.string.repeated_messages),
                    icon = Icons.Default.ReplyAll
                ) {
                    navController.navigate(ScenariosDestinations.OTHER_MESSAGES)
                }
                MenuButton(
                    title = stringResource(R.string.scenarios_paid),
                    icon = Icons.Default.TrendingUp
                ) {
                    navController.navigate(ScenariosDestinations.PAID_AD)
                }
                MenuButton(
                    title = stringResource(R.string.price_changing),
                    icon = Icons.Default.PriceChange
                ) {
                    navController.navigate(ScenariosDestinations.PRICE_CHANGED)
                }
                MenuButton(
                    title = stringResource(R.string.scenarios_archive),
                    icon = Icons.Default.Inventory2
                ) {
                    navController.navigate(ScenariosDestinations.ARCHIVED)
                }
                MenuButton(
                    title = stringResource(R.string.scenarios_sold),
                    icon = Icons.Default.Sell
                ) {
                    navController.navigate(ScenariosDestinations.SOLD)
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuButton(title: String, icon: ImageVector? = null, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = Modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Constants.defaultPadding)
                    .heightIn(60.dp, 120.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                icon?.let { Icon(icon, contentDescription = icon.name) }

                Text(title)
            }
//            Divider(
//                modifier = Modifier
//                    .fillMaxWidth()
//            )
        }
    }


}