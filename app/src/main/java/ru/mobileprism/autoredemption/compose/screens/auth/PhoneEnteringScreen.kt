package ru.mobileprism.autoredemption.compose.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CrueltyFree
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.viewModel
import ru.mobileprism.autoredemption.R
import ru.mobileprism.autoredemption.compose.custom.MainButton
import ru.mobileprism.autoredemption.compose.custom.ModalLoadingDialog
import ru.mobileprism.autoredemption.model.entities.PhoneAuthEntity
import ru.mobileprism.autoredemption.utils.BaseViewState
import ru.mobileprism.autoredemption.utils.Constants
import ru.mobileprism.autoredemption.utils.PhoneNumberVisualTransformation
import ru.mobileprism.autoredemption.utils.showError
import ru.mobileprism.autoredemption.viewmodels.PhoneEnteringViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PhoneEnteringScreen(onNext: (PhoneAuthEntity) -> Unit) {

    val viewModel: PhoneEnteringViewModel by viewModel()
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    val phoneNum = viewModel.phoneNum.collectAsState()
    val isPhoneSucceed = viewModel.isPhoneSucceed.collectAsState()

    var invalidPhoneDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is BaseViewState.Success -> {
                onNext(state.data)
                viewModel.resetState()
            }
            is BaseViewState.Error -> {
                context.applicationContext.showError(state)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val onReady: () -> Unit = {
        keyboardController?.hide()
        if (isPhoneSucceed.value) viewModel.authenticate()
        else invalidPhoneDialog = true
    }

    ModalLoadingDialog(
        onDismiss = { viewModel.cancelLoading() },
        isLoading = uiState is BaseViewState.Loading
    )

    if (invalidPhoneDialog)
        AlertDialog(onDismissRequest = { invalidPhoneDialog = false },
            title = { Text(stringResource(R.string.wrong_phone_number_format)) },
            text = { Text(stringResource(R.string.try_again_message)) },
            confirmButton = {
                Button(onClick = { invalidPhoneDialog = false }) {
                    Text(stringResource(R.string.label_ok))
                }
            })

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            AuthTopAppBar(actions = {
                IconButton(onClick = { viewModel.loginTestUser() }) {
                    Icon(Icons.Default.CrueltyFree, "")
                }
            })
        }
    ) {

        Column(
            modifier = Modifier
                .padding(30.dp)
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.h4,
                )
                Text(text = stringResource(R.string.app_description))
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = phoneNum.value,
                    onValueChange = { newValue: String ->
                        viewModel.onPhoneSet(newValue)
                    },
                    readOnly = uiState is BaseViewState.Loading,
                    leadingIcon = {
                        Image(
                            painterResource(R.drawable.russian_flag),
                            "",
                            modifier = Modifier.size(25.dp)
                        )
                    }, singleLine = true,
                    trailingIcon = {
                        AnimatedVisibility(
                            phoneNum.value.length > 2,
                            enter = fadeIn(), exit = fadeOut()
                        ) {
                            IconButton(onClick = viewModel::resetPhoneNum) {
                                Icon(Icons.Default.Close, Icons.Default.Close.name)
                            }
                        }
                    },
                    textStyle = TextStyle(
                        MaterialTheme.colors.onSurface, fontSize = 24.sp,
                        textAlign = TextAlign.Start, fontWeight = FontWeight.SemiBold
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        onReady()
                    }),
                    isError = viewModel.isPhoneError.value,
                    visualTransformation = PhoneNumberVisualTransformation()
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                MainButton(
                    modifier = Modifier,
                    content = { Text(text = stringResource(R.string.proceed)) },
                    onClick = onReady
                )
            }

        }

    }
}

