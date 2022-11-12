package ru.mobileprism.autoredemption.compose.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import org.koin.androidx.compose.getViewModel
import ru.mobileprism.autoredemption.R
import ru.mobileprism.autoredemption.compose.custom.MainButton
import ru.mobileprism.autoredemption.compose.custom.ModalLoadingDialog
import ru.mobileprism.autoredemption.compose.screens.home.AutoBotTextField
import ru.mobileprism.autoredemption.model.entities.SmsConfirmEntity
import ru.mobileprism.autoredemption.utils.BaseViewState
import ru.mobileprism.autoredemption.utils.showError
import ru.mobileprism.autoredemption.viewmodels.ChooseCityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseCityScreen(smsConfirmEntity: SmsConfirmEntity, upPress: () -> Unit, onNext: () -> Unit) {

    val viewModel: ChooseCityViewModel = getViewModel()
    val context = LocalContext.current
    val cities = viewModel.cities.collectAsState()
    val timezones = viewModel.timezones.collectAsState()
    val chosenValues = viewModel.chosenCityAndTimezone.collectAsState()

    val valuesState = viewModel.valuesState.collectAsState()
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.value) {
        when(val state = uiState.value) {
            is BaseViewState.Success -> onNext()
            is BaseViewState.Error -> {
                context.showError(state)
            }
            else -> {}
        }
    }

    ModalLoadingDialog(isLoading = valuesState.value is BaseViewState.Loading || uiState.value is BaseViewState.Loading)

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()
        .imePadding(),
        topBar = { AuthTopAppBar(title = "Регистрация", upPress) }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier, verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Пожалуйста, выберите ваш город и часовой пояс для отправки сообщений",
                    style = MaterialTheme.typography.titleMedium
                )

                ChooseOutlineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Город",
                    chosenValue = chosenValues.value.city?.name,
                    resetChosenValue = { viewModel.resetChosenCity() }
                ) { text ->
                    cities.value.filter { it.name.startsWith(text) }.forEach { city ->
                        DropdownMenuItem(onClick = { viewModel.onCitySelected(city) },
                            text = { Text(text = city.name) })
                    }
                }

                ChooseOutlineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Часовой пояс",
                    chosenValue = chosenValues.value.timezone?.name,
                    resetChosenValue = { viewModel.resetChosenTimezone() }
                ) { text ->
                    timezones.value.filter { it.name.startsWith(text) }.forEach { timezone ->
                        DropdownMenuItem(onClick = { viewModel.onTimezoneSelected(timezone) },
                            text = { Text(text = timezone.name) })
                    }
                }

            }

            MainButton(
                modifier = Modifier.weight(1f, false),
                enabled = viewModel.couldSaveValues.collectAsState().value,
                content = { Text(text = stringResource(R.string.proceed)) },
                onClick = viewModel::saveChosenValues
            )

        }

    }
}

@Composable
fun ChooseOutlineTextField(
    modifier: Modifier,
    placeholder: String,
    chosenValue: String?,
    resetChosenValue: () -> Unit,
    dropdownColumn: @Composable() (ColumnScope.(String) -> Unit)
) {
    Box {
        var chosenText by remember { mutableStateOf("") }
        var dropDownExpanded by remember { mutableStateOf(false) }

        LaunchedEffect(chosenValue) {
            if (chosenValue != null) dropDownExpanded = false
        }

        AutoBotTextField(
            modifier = modifier.onFocusChanged {
                if (it.hasFocus && chosenValue == null) dropDownExpanded = true
            },
            readOnly = chosenValue != null,
            value = chosenValue ?: chosenText,
            onValueChange = {
                chosenText = it
                dropDownExpanded = true
            },
            placeholder = placeholder,
            trailingIcon = {
                if (chosenValue != null) {
                    IconButton(onClick = resetChosenValue) {
                        Icon(Icons.Default.Close, "")
                    }
                } else
                    AnimatedVisibility(visible = !dropDownExpanded && chosenText.isEmpty()) {
                        IconButton(onClick = { dropDownExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, "")
                        }
                    }
            }
        )
        DropdownMenu(expanded = dropDownExpanded, properties = PopupProperties(
            focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true
        ), onDismissRequest = { dropDownExpanded = false }) {
            dropdownColumn(chosenText)
        }
    }
}
