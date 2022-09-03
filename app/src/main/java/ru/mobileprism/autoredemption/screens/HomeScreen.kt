package ru.mobileprism.autoredemption.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.mobileprism.autoredemption.*
import ru.mobileprism.autoredemption.datastore.AppSettings
import ru.mobileprism.autoredemption.datastore.AppSettingsEntity


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(toSettings: () -> Unit) {

    val settings: AppSettings = get()
    val appSettings by settings.appSettings.collectAsState(AppSettingsEntity())
    val testNumbers by settings.testNumbers.collectAsState(emptySet())

    val context = LocalContext.current
    val isServiceRunning = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        isServiceRunning.value =
            context.isServiceRunning(ForegroundService::class.java)
    }

    val smsPermissionState = rememberPermissionState(
        Manifest.permission.SEND_SMS
    )

    DisposableEffect(Unit) {
        smsPermissionState.launchPermissionRequest()
        onDispose { }
    }

    DisposableEffect(key1 = context) {

        onDispose { }
    }

    val coroutineScope = rememberCoroutineScope()

    val sheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            AddNumSheet(sheetState = sheetState, onAdd = { number ->
                coroutineScope.launch {
                    if (testNumbers.contains(number)) {
                        Toast.makeText(context, "Номер есть в списке", Toast.LENGTH_SHORT).show()
                    } else settings.saveTestNumbers(testNumbers.plus(number))
                }
            })
        }) {

        Scaffold(
            modifier = Modifier,
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                Crossfade(targetState = isServiceRunning.value) {
                    when (it) {
                        true -> {
                            FloatingActionButton(
                                backgroundColor = MaterialTheme.colors.error,
                                onClick = {
                                    context.stopService()
                                    Toast.makeText(
                                        context,
                                        "Сервис остановлен",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isServiceRunning.value = false
                                }) {
                                Icon(Icons.Default.Close, "")
                            }
                        }
                        false -> {
                            FloatingActionButton(onClick = {
                                context.startSmsService(if (appSettings.debugMode) Constants.DEBUG_NUMBERS else testNumbers.toList())
                                Toast.makeText(
                                    context,
                                    "Сервис запущен",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isServiceRunning.value = true
                            }) {
                                Row(modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(text = "Начать отправку SMS")
                                    Icon(Icons.Default.Send, "")
                                }
                            }
                        }
                    }
                }
            },
            topBar = {
                TopAppBar(title = { Text(text = "АвтоВыкуп") },
                    actions = {
                        IconButton(enabled = appSettings.debugMode.not(), onClick = {
                            coroutineScope.launch {
                                sheetState.show()
                            }
                        }) { Icon(Icons.Default.Add, "") }
                        IconButton(onClick = toSettings) {
                            Icon(Icons.Default.Settings, "")
                        }
                    })
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {
                if (appSettings.debugMode) {
                    Constants.DEBUG_NUMBERS.forEach { number ->
                        NumberItem(
                            number,
                            onDelete = null
                        )
                    }
                } else {
                    testNumbers.forEach { number ->
                        NumberItem(number,
                            onDelete = {
                                coroutineScope.launch {
                                    settings.saveTestNumbers(testNumbers.minus(number))
                                }
                            })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun AddNumSheet(sheetState: ModalBottomSheetState, onAdd: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val coroutineScope = rememberCoroutineScope()
    val numToAdd = remember { mutableStateOf("+7") }
    val context = LocalContext.current


    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Добавление нового номера")
        OutlinedTextField(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = numToAdd.value,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            onValueChange = { if (isPhoneNumValid(it)) numToAdd.value = it },
            visualTransformation = PhoneNumberVisualTransformation()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(enabled = numToAdd.value.length == 12, onClick = {
                if (isPhoneNumValid(numToAdd.value)) {
                    onAdd(numToAdd.value)
                    numToAdd.value = "+7"
                    keyboardController?.hide()
                    coroutineScope.launch { sheetState.hide() }
                } else {
                    Toast.makeText(
                        context,
                        "Неверный формат номера!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }) {
                Text(text = "Добавить")
            }
            Spacer(modifier = Modifier.size(4.dp))
            OutlinedButton(
                onClick = {
                    numToAdd.value = "+7"
                    keyboardController?.hide()
                    coroutineScope.launch { sheetState.hide() }
                },
            ) {
                Text(text = "Отменить")
            }

        }

        /*DisposableEffect(Unit) {
            focusRequester.requestFocus()
            onDispose { }
        }*/
    }
}

@Composable
fun NumberItem(number: String, onDelete: (() -> Unit)?) {
    Column {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = number)
            onDelete?.let {
                IconButton(onClick = { onDelete() }) {
                    Icon(Icons.Default.Delete, "")
                }
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())
    }

}

fun isPhoneNumValid(newValue: String): Boolean {
    return when (newValue.length) {
        in 0..2 -> newValue.startsWith("+7")
        in 3..12 -> {
            newValue.startsWith("+7") && newValue.last().digitToIntOrNull() != null
        }
        else -> false
    }
}


private fun Context.startSmsService(numbers: List<String>) {
    if (numbers.isEmpty()) {
        Toast.makeText(this, "Нет номеров для отправки сообщений!", Toast.LENGTH_SHORT).show()
    } else {

        val serviceIntent = Intent(this, ForegroundService::class.java)
        serviceIntent.putExtra("inputExtra", "Сервис для автоматической отправки сообщений")
        serviceIntent.putExtra("numbers", numbers.toTypedArray())
        applicationContext.startForegroundService(serviceIntent)

        try {
            val packageName = packageName
            val pm = getSystemService(ComponentActivity.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }

    }

}

private fun Context.stopService() {
    val serviceIntent = Intent(this, ForegroundService::class.java)
    stopService(serviceIntent)
}
