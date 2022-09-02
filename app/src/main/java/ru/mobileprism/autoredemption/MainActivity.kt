package ru.mobileprism.autoredemption

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.work.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import ru.mobileprism.autoredemption.ui.theme.AutoRedemptionTheme
import ru.mobileprism.autoredemption.workmanager.SendSMSWorker
import java.time.Duration
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    @SuppressLint("BatteryLife")
    @OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoRedemptionTheme {
                val context = LocalContext.current
                val isServiceRunning = remember { mutableStateOf(false) }

                val smsPermissionState = rememberPermissionState(
                    android.Manifest.permission.SEND_SMS
                )

                DisposableEffect(Unit) {
                    smsPermissionState.launchPermissionRequest()
                    isServiceRunning.value = context.isServiceRunning(ForegroundService::class.java)
                    onDispose { }
                }

                DisposableEffect(key1 = context) {
                    try {
                        val packageName = packageName
                        val pm = getSystemService(POWER_SERVICE) as PowerManager
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
                    onDispose { }
                }

                val workManager = remember { WorkManager.getInstance(context) }

                val coroutineScope = rememberCoroutineScope()

                val numbers = remember { mutableStateListOf<String>() }
                val sheetState =
                    rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

                BackHandler(sheetState.isVisible) {
                    coroutineScope.launch { sheetState.hide() }
                }

                ModalBottomSheetLayout(
                    sheetState = sheetState,
                    sheetContent = {
                        AddNumSheet(sheetState = sheetState, onAdd = { numbers.add(it) })
                    }) {

                    Scaffold(
                        floatingActionButton = {
                            Crossfade(targetState = isServiceRunning.value) {
                                when (it) {
                                    true -> {
                                        FloatingActionButton(onClick = { stopService() }) {
                                            Icon(Icons.Default.Close, "")
                                        }
                                    }
                                    false -> {
                                        FloatingActionButton(onClick = {
                                            startService()
                                            /*workManager.enqueueUniquePeriodicWork(
                                                SendSMSWorker.NAME, ExistingPeriodicWorkPolicy.REPLACE,
                                                getSendSMSWork(numbers)
                                            )*/
                                        }) {
                                            Icon(Icons.Default.Send, "")
                                        }
                                    }
                                }
                            }
                        },
                        topBar = {
                            TopAppBar(title = { Text(text = "АвтоВыкуп") },
                                actions = {
                                    IconButton(onClick = {
                                        coroutineScope.launch {
                                            sheetState.show()
                                        }
                                    }) { Icon(Icons.Default.Add, "") }
                                })
                        }
                    ) {
                        Column(modifier = Modifier.padding(it)) {
                            numbers.forEach { number ->
                                NumberItem(number,
                                    onDelete = {
                                        numbers.remove(number)
                                    })
                            }
                        }
                    }
                }
            }
        }
    }

    fun startService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android")
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        stopService(serviceIntent)
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
        }*/
    }
}

@Composable
fun NumberItem(number: String, onDelete: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = number)
            IconButton(onClick = { onDelete() }) {
                Icon(Icons.Default.Delete, "")
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

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AutoRedemptionTheme {
        Greeting("Android")
    }
}

fun getSendSMSWork(numbers: List<String>): PeriodicWorkRequest {
    val myData: Data = Data.Builder()
        .apply {
            if (BuildConfig.DEBUG) {
                putStringArray(
                    SendSMSWorker.NUMBERS_ARG,
                    Constants.DEBUG_NUMBERS.toTypedArray()
                )
            } else putStringArray(SendSMSWorker.NUMBERS_ARG, numbers.toTypedArray())
        }
        .build()

    return PeriodicWorkRequestBuilder<SendSMSWorker>(10, TimeUnit.MINUTES)
        .setInputData(myData)
        .setConstraints(
            Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()
        )
        .apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setBackoffCriteria(BackoffPolicy.EXPONENTIAL, Duration.ofSeconds(5))
            }
        }.build()
}

@Suppress("DEPRECATION") // Deprecated for third party Services.
fun <T> Context.isServiceRunning(service: Class<T>) =
    (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }