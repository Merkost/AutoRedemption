package ru.mobileprism.autoredemption.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.viewModel
import ru.mobileprism.autoredemption.utils.PhoneNumberVisualTransformation

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(onNext: (/*PhoneRegisterEntity*/) -> Unit) {

    //val viewModel: PhoneEnteringViewModel by viewModel()
    val context = LocalContext.current

    //val uiState by viewModel.uiState.collectAsState()

    val phoneNum = remember { mutableStateOf("+7") }
    var invalidPhoneDialog by remember { mutableStateOf(false) }

    /*LaunchedEffect(uiState) {
        when (val state = uiState) {
            is BaseViewState.Success -> {
                onNext(state.data)
                viewModel.resetState()
            }
            is BaseViewState.Error -> {
                showError(context.applicationContext, state.text)
            }
            else -> {}
        }
    }*/

    val keyboardController = LocalSoftwareKeyboardController.current

    val onReady: () -> Unit = {
        keyboardController?.hide()
        onNext()
        /*if (viewModel.isPhoneError.value || phoneNum.value.length != 12)
            invalidPhoneDialog = true
        else viewModel.authentificate()*/
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier.padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "АвтоБот", style = MaterialTheme.typography.h5)

                Text(text = "Автоматически отправляйте сообщения о покупке авто при снижении цены")
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
                        if (phoneNum.value.take(2) == "+7"/*viewModel.isPhoneNumValid(newValue)*/)
                            phoneNum.value = newValue
                    },
                    /*readOnly = uiState is BaseViewState.Loading,*/
                    leadingIcon = {
                        Image(
                            painterResource(ru.mobileprism.autoredemption.R.drawable.russian_flag),
                            "",
                            modifier = Modifier.size(25.dp)
                        )
                    }, singleLine = true,
                    trailingIcon = {
                        AnimatedVisibility(
                            phoneNum.value.length > 2,
                            enter = fadeIn(), exit = fadeOut()
                        ) {
                            IconButton(onClick = {
                                /*if (uiState !is BaseViewState.Loading)*/ phoneNum.value = "+7"
                            }) {
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
                    /*isError = viewModel.isPhoneError.value*/
                    visualTransformation = PhoneNumberVisualTransformation()
                )

                Button(
                    modifier = Modifier,
                    content = {
                        Text(text = "Продолжить")
                    },
                    onClick = onReady
                )
            }


        }

    }
}