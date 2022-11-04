package ru.mobileprism.autoredemption.compose.screens

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.mobileprism.autoredemption.ChooseSimScreen
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.datastore.AppSettingsEntity

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(upPress: () -> Unit, toLogs: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val settings: AppSettings = get()
    val context = LocalContext.current
    val appSettings = settings.appSettingsEntity.collectAsState(initial = AppSettingsEntity())

    val numbersDelay = remember(appSettings.value) {
        mutableStateOf(
            TextFieldValue(
                text = appSettings.value.messagesDelay.toString(),
                selection = TextRange(appSettings.value.messagesDelay.toString().length),
                composition = TextRange(appSettings.value.messagesDelay.toString().length)
            )
        )
    }

    val currentVersion = remember {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }


    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(title = {
                Text(text = "Настройки")
            }, navigationIcon = {
                IconButton(onClick = upPress) {
                    Icon(Icons.Default.ArrowBack, Icons.Default.ArrowBack.name)
                }
            },
                actions = {
                    IconButton(onClick = {
                        //as a list of strings
                        toLogs()
                    }) {
                        Icon(Icons.Default.List, "")
                    }
                })
        }
    ) {

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingsRow("Тестовые номера") {
                Checkbox(checked = appSettings.value.testMode, onCheckedChange = {
                    coroutineScope.launch {
                        settings.saveAppSettings(appSettings.value.copy(testMode = it))
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
            SettingsRow("Задержка между отправками сообщений") {

                TextField(
                    shape = RoundedCornerShape(8.dp),
                    value = numbersDelay.value,
                    onValueChange = { newDelay ->
                        if (newDelay.text.toLongOrNull() != null && newDelay.text.toLongOrNull() in 0L..100000L) {
                            coroutineScope.launch {
                                settings.saveAppSettings(appSettings.value.copy(messagesDelay = newDelay.text.toLong()))
                            }
                        } else if (newDelay.text.isEmpty()) {
                            numbersDelay.value = numbersDelay.value.copy(
                                text = "0",
                                selection = TextRange(1)
                            )
                            coroutineScope.launch {
                                settings.saveAppSettings(appSettings.value.copy(messagesDelay = 0L))
                            }
                        }
                    },
                    modifier = Modifier.weight(0.8f),
                    /*.onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            numbersDelay.value = numbersDelay.value.copy(
                                selection = TextRange(0,numbersDelay.value.text.length),
                            )
                        }
                    },*/
                    trailingIcon = {
                        Text(text = "мс")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }

            SettingsColumn(text = "Выбор сим карты") {
                val readSimPermission = rememberPermissionState(permission = Manifest.permission.READ_PHONE_STATE)
                if (readSimPermission.status.isGranted.not()) {
                    IconButton(onClick = { readSimPermission.launchPermissionRequest() }) {
                        Text(text = "Необходимо разрешение!")
                    }
                } else {
                    ChooseSimScreen()
                }
            }
            /*SettingsColumn("Показывать уведомления при каждой отправке смс?") {
                Checkbox(checked = appSettings.value.notificationOnWork, onCheckedChange = {
                    coroutineScope.launch {
                        settings.saveAppSettings(appSettings.value.copy(timeInText = it))
                    }
                })
            }*/
            /*SettingsRow("Основной текст сообщения") {
                TextField(
                    placeholder = {
                        Text(text = "Сообщение")
                    },
                    shape = RoundedCornerShape(8.dp),
                    value = appSettings.value.messageText,
                    onValueChange = { newText ->
                        coroutineScope.launch {
                            settings.saveAppSettings(appSettings.value.copy(messageText = newText))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }*/


            Text(
                text = "Версия $currentVersion",
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f, true)
            )
        }


    }
}

@Composable
fun SettingsRow(text: String, action: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .padding(horizontal = 4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            modifier = Modifier
                .weight(1f, false)
                .padding(end = 8.dp),
        )
        action()
    }
}

@Composable
fun SettingsColumn(text: String, action: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .padding(horizontal = 4.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = text,
            modifier = Modifier
            //.padding(end = 8.dp)
            //.weight(1f, false),
        )
        action()
    }
}