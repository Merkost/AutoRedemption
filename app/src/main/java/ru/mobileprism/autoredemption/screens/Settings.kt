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

            Row(modifier = Modifier.padding(8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Тестовый режим")
                Checkbox(checked = appSettings.value.debugMode, onCheckedChange = {
                    coroutineScope.launch {
                        settings.saveAppSettings(appSettings.value.copy(debugMode = it))
                    }
                })
            }

        }

    }
}