package ru.mobileprism.autobot.compose.screens.auth

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import org.koin.androidx.compose.getViewModel
import ru.mobileprism.autobot.R
import ru.mobileprism.autobot.compose.custom.DefaultAlertDialog3
import ru.mobileprism.autobot.compose.custom.MainButton
import ru.mobileprism.autobot.compose.custom.ModalLoadingDialog
import ru.mobileprism.autobot.compose.custom.SmallErrorViewVertical
import ru.mobileprism.autobot.compose.screens.home.AutoBotTextField
import ru.mobileprism.autobot.compose.screens.home.ListSpacer
import ru.mobileprism.autobot.fragment.CityFragment
import ru.mobileprism.autobot.utils.BaseViewState
import ru.mobileprism.autobot.utils.Constants
import ru.mobileprism.autobot.utils.showError
import ru.mobileprism.autobot.viewmodels.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(upPress: () -> Unit, onAuth: () -> Unit, onNext: () -> Unit) {

    val viewModel: RegisterViewModel = getViewModel()
    val context = LocalContext.current
    val cities: State<List<CityFragment>> = viewModel.cities.collectAsState()
    val chosenValues = viewModel.chosenValues.collectAsState()

    val valuesState = viewModel.valuesState.collectAsState()
    val uiState = viewModel.uiState.collectAsState()
    var cancelRegisterDialog by remember {
        mutableStateOf(false)
    }
    if (cancelRegisterDialog) {
        DefaultAlertDialog3(primaryText = "Вы хотите прервать регистрацию?",
            secondaryText = "Вам будет необходимо выполнить вход снова",
            onPositiveClick = {
                viewModel.cancelRegister()
                onAuth()
            },
            onDismiss = { cancelRegisterDialog = false },
            onDismissClick = { cancelRegisterDialog = false })
    }

    BackHandler {
        cancelRegisterDialog = true
    }

    LaunchedEffect(uiState.value) {
        when (val state = uiState.value) {
            is BaseViewState.Success -> onNext()
            is BaseViewState.Error -> {
                context.showError(state.autoBotError)
            }
            else -> {}
        }
    }

    ModalLoadingDialog(isLoading = valuesState.value is BaseViewState.Loading || uiState.value is BaseViewState.Loading)

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .consumedWindowInsets(WindowInsets.navigationBars),
        topBar = { AuthTopAppBar(title = stringResource(id = R.string.registration)) }) {
        Crossfade(
            targetState = valuesState.value,
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 30.dp)
        ) {
            when (it) {
                is BaseViewState.Loading -> {}
                is BaseViewState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        SmallErrorViewVertical(onRetry = viewModel::retry)
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = Constants.smallPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier, verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            AutoBotTextField(
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = stringResource(id = R.string.your_name),
                                value = chosenValues.value.name,
                                onValueChange = viewModel::onNameSelected,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Next
                                )
                            )

                            ChooseOutlineTextField(modifier = Modifier.fillMaxWidth(),
                                placeholder = "Город",
                                chosenValue = chosenValues.value.city?.label,
                                enteredText = chosenValues.value.cityText,
                                onTextChanged = viewModel::onNewCityTextInput,
                                resetChosenValue = { viewModel.resetChosenCity() }) {
                                if (chosenValues.value.cityText.isEmpty()) {
                                    DropdownMenuItem(text = { Text(text = "Начните вводить название города") },
                                        onClick = {})
                                } else {
                                    cities.value.take(6).forEach { city ->
                                        DropdownMenuItem(onClick = { viewModel.onCitySelected(city) },
                                            text = { Text(text = city.label) })
                                    }
                                }
                            }
                        }
                        ListSpacer()

                        MainButton(
                            modifier = Modifier,
                            enabled = viewModel.couldSaveValues.collectAsState().value,
                            content = { Text(text = stringResource(R.string.proceed)) },
                            onClick = viewModel::saveChosenValues
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ChooseOutlineTextField(
    modifier: Modifier,
    placeholder: String,
    chosenValue: String?,
    enteredText: String,
    onTextChanged: (String) -> Unit,
    resetChosenValue: () -> Unit,
    dropdownColumn: @Composable() (ColumnScope.() -> Unit)
) {
    Box {
        var dropDownExpanded by remember { mutableStateOf(false) }

        LaunchedEffect(chosenValue) {
            if (chosenValue != null) dropDownExpanded = false
        }

        AutoBotTextField(modifier = modifier.onFocusChanged {
            if (it.hasFocus && chosenValue == null) dropDownExpanded = true
        },
            enabled = chosenValue == null,
            singleLine = true,
            value = chosenValue ?: enteredText,
            onValueChange = {
                onTextChanged(it)
                dropDownExpanded = true
            },
            placeholder = placeholder,
            trailingIcon = {
                if (chosenValue != null) {
                    IconButton(onClick = {
                        resetChosenValue()
                    }) {
                        Icon(Icons.Default.Close, "")
                    }
                } else AnimatedVisibility(visible = !dropDownExpanded && enteredText.isEmpty()) {
                    IconButton(onClick = { dropDownExpanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, "")
                    }
                }
            })
        DropdownMenu(expanded = dropDownExpanded, properties = PopupProperties(
            focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true
        ), onDismissRequest = { dropDownExpanded = false }) {
            dropdownColumn()
        }
    }
}
