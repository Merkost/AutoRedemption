package ru.mobileprism.autoredemption.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.mobileprism.autoredemption.datastore.AppSettings
import ru.mobileprism.autoredemption.datastore.AppSettingsEntity

@Composable
fun SettingsScreen(upPress: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val settings: AppSettings = get()
    val appSettings = settings.appSettings.collectAsState(initial = AppSettingsEntity())

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(title = {
                Text(text = "Настройки")
            }, navigationIcon = {
                IconButton(onClick = upPress) {
                    Icon(Icons.Default.ArrowBack, Icons.Default.ArrowBack.name)
                }
            })
        }
    ) {

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SettingsRow("Тестовые номера") {
                Checkbox(checked = appSettings.value.debugMode, onCheckedChange = {
                    coroutineScope.launch {
                        settings.saveAppSettings(appSettings.value.copy(debugMode = it))
                    }
                })
            }
            SettingsRow("Добавлять дату и время к тексту сообщения") {
                Checkbox(checked = appSettings.value.timeInText, onCheckedChange = {
                    coroutineScope.launch {
                        settings.saveAppSettings(appSettings.value.copy(timeInText = it))
                    }
                })
            }
            /*SettingsRow("Основной текст сообщения") {
                Checkbox(checked = appSettings.value.debugMode, onCheckedChange = {
                    coroutineScope.launch {
                        settings.saveAppSettings(appSettings.value.copy(debugMode = it))
                    }
                })
            }*/

        }

    }
}

@Composable
fun SettingsRow(text: String, action: @Composable () -> Unit) {
    Row(modifier = Modifier.padding(8.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = text, modifier = Modifier.weight(1f, false).padding(end = 8.dp), )
        action()
    }
}