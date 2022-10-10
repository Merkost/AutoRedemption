package ru.mobileprism.autoredemption.compose.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import ru.mobileprism.autoredemption.*
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.datastore.AppSettingsEntity
import ru.mobileprism.autoredemption.utils.*
import ru.mobileprism.autoredemption.viewmodels.HomeViewModel


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(toSettings: () -> Unit) {

    val settings: AppSettings = get()
    val viewModel: HomeViewModel = getViewModel()
    val appSettingsEntity by settings.appSettingsEntity.collectAsState(AppSettingsEntity())

    val context = LocalContext.current
    val isServiceRunning = rememberSaveable { mutableStateOf(false) }
    val lazyState = rememberLazyListState()


    LaunchedEffect(key1 = Unit) {
        isServiceRunning.value =
            context.isServiceRunning(ForegroundService::class.java)
    }

    /*LaunchedEffect(appSettingsEntity.testNumbers) {
        lazyState.animateScrollToItem(appSettingsEntity.testNumbers.size-1)
    }*/



    val smsPermissionState = rememberPermissionState(
        Manifest.permission.SEND_SMS
    )

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
                    if (appSettingsEntity.numbers.contains(number)) {
                        Toast.makeText(context, "Номер есть в списке", Toast.LENGTH_SHORT).show()
                    } else {
                        /*settings.saveAppSettings(appSettingsEntity.copy(numbers = appSettingsEntity.numbers + number))*/
                        viewModel.addRealNumber(number)
                    }
                }
            })
        }) {

        Scaffold(
            modifier = Modifier,
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                FloatingActionButton(modifier = Modifier.animateContentSize(), onClick = {
                    when (isServiceRunning.value) {
                        true -> {
                            context.stopService()
                            context.showToast("Сервис остановлен")

                            isServiceRunning.value = false
                        }
                        false -> {
                            //context.autoStart()

                            if (smsPermissionState.hasPermission) {
                                context.startSmsService()
                                context.showToast("Сервис запущен")

                                isServiceRunning.value = true
                            } else {
                                smsPermissionState.launchPermissionRequest()
                                context.showToast("Необходимо разрешение на отправку уведомлений!")

                            }
                        }
                    }
                }) {
                    Crossfade(targetState = isServiceRunning.value) {
                        when (it) {
                            true -> Icon(Icons.Default.Close, "")
                            false ->
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(text = "Начать отправку SMS")
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
                            if (appSettingsEntity.testMode.not()) {
                                coroutineScope.launch { sheetState.show() }
                            } else {
                                viewModel.addTestNumber()
                            }
                        }) { Icon(Icons.Default.Add, "") }
                        IconButton(onClick = toSettings) {
                            Icon(Icons.Default.Settings, "")
                        }
                    })
            }
        ) {
            LazyColumn(
                state = lazyState,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)

            ) {
                if (appSettingsEntity.testMode) {
                    item {
                        Row(modifier = Modifier.padding(8.dp)) {
                            Text(text = "Тестовый режим!", style = MaterialTheme.typography.body1)
                        }
                    }
                    if (appSettingsEntity.testNumbers.isEmpty())
                        item {
                            Row(modifier = Modifier.padding(32.dp)) {
                                Text(text = "Нажмите на \"+\", чтобы добавить тестовые номера")
                            }
                        }
                    items(appSettingsEntity.testNumbers.toList().reversed()) { number ->
                        NumberItem(
                            number,
                            onDelete = { viewModel.deleteTestNumber(number) }
                        )
                    }
                } else {
                    items(appSettingsEntity.numbers.reversed()) { number ->
                        NumberItem(number,
                            onDelete = { viewModel.deleteRealNumber(number) })
                    }
                }
                item { Spacer(modifier = Modifier.size(200.dp)) }
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
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = number)
            onDelete?.let {
                IconButton(onClick = { onDelete() }) {
                    Icon(
                        Icons.Default.Delete, "",
                        tint = MaterialTheme.colors.error
                    )
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