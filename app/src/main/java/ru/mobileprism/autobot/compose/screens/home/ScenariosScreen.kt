package ru.mobileprism.autobot.compose.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.mobileprism.autobot.compose.ScenariosDestinations
import ru.mobileprism.autobot.compose.custom.DefaultColumn
import ru.mobileprism.autobot.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenariosScreen(upPress: () -> Unit, navController: NavController) {

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Сценарии") },
        )
    }) {
        DefaultColumn(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(it),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Constants.smallPadding),
                modifier = Modifier.padding(top = Constants.smallPadding)
            ) {
                MenuButton(title = "Первое сообщение") {
                    navController.navigate(ScenariosDestinations.FIRST_MESSAGE)
                }
                MenuButton(title = "Повторные сообщения")
                MenuButton(title = "Поднятые объяления")
                MenuButton(title = "Изменение стоимости")
                MenuButton(title = "Архивные сообщения")
                MenuButton(title = "Проданные объявления")
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuButton(title: String, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = Constants.defaultPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Constants.defaultPadding)
                    .heightIn(60.dp, 120.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title)
            }
//            Divider(
//                modifier = Modifier
//                    .fillMaxWidth()
//            )
        }
    }


}