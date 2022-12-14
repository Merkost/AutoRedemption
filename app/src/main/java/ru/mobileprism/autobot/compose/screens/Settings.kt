package ru.mobileprism.autobot.compose.screens

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
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
import ru.mobileprism.autobot.model.datastore.AppSettings
import ru.mobileprism.autobot.model.datastore.AppSettingsEntity
import ru.mobileprism.autobot.utils.Constants

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(upPress: () -> Unit, toLogs: () -> Unit, toPermissions: () -> Unit) {
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


    Scaffold(modifier = Modifier, topBar = {
        TopAppBar(title = {
            Text(text = "Настройки")
        }, navigationIcon = {
            IconButton(onClick = upPress) {
                Icon(Icons.Default.ArrowBack, Icons.Default.ArrowBack.name)
            }
        }, actions = {
            IconButton(onClick = {
                //as a list of strings
                toLogs()
            }) {
                Icon(Icons.Default.List, "")
            }
        })
    }) {

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingsRow(text = "Тестовые номера", onClick = {
                coroutineScope.launch {
                    settings.saveAppSettings(appSettings.value.copy(testMode = appSettings.value.testMode.not()))
                }
            }) {
                Checkbox(checked = appSettings.value.testMode, onCheckedChange = {
                    coroutineScope.launch {
                        settings.saveAppSettings(appSettings.value.copy(testMode = it))
                    }
                })
            }
            SettingsRow(text = "Добавлять дату и время к тексту сообщения", onClick = {
                coroutineScope.launch {
                    settings.saveAppSettings(
                        appSettings.value.copy(
                            timeInText = appSettings.value.timeInText.not()
                        )
                    )
                }
            }) {
                Checkbox(checked = appSettings.value.timeInText, onCheckedChange = {
                    coroutineScope.launch {
                        settings.saveAppSettings(
                            appSettings.value.copy(
                                timeInText = it
                            )
                        )
                    }
                })
            }
            SettingsRow(text = "Задержка между отправками сообщений") {

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
                                text = "0", selection = TextRange(1)
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

            SettingsRow(text = "Разрешения для работы", onClick = {
                toPermissions()
            }, action = null)

            SettingsColumn(text = "Выбор сим карты") {
                val readSimPermission =
                    rememberPermissionState(permission = Manifest.permission.READ_PHONE_STATE)
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
                text = "Версия $currentVersion", modifier = Modifier
                    .padding(Constants.superSmallPadding).padding(top = Constants.largePadding)
                    .weight(1f, true)
            )
        }


    }
}

@Composable
fun SettingsRow(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit)? = null,
    action: @Composable (RowScope.() -> Unit)?,
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
        .clickable(enabled = onClick != null) {
            if (onClick != null) {
                onClick()
            }
        }

        ) {

    Row(modifier = Modifier
        .fillMaxWidth().heightIn(60.dp, 120.dp).padding(Constants.smallPadding)
        .padding(Constants.smallPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = text,
            modifier = Modifier
                .weight(1f, false)
                .padding(end = Constants.smallPadding),
        )
        action?.let { action() }
    }
        Divider(modifier = Modifier.fillMaxWidth())
    }

}

@Composable
fun SettingsColumn(text: String, action: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .padding(Constants.smallPadding)
            .padding(horizontal = Constants.smallPadding)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Constants.superSmallPadding),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = text, modifier = Modifier
            //.padding(end = 8.dp)
            //.weight(1f, false),
        )
        action()
    }
}