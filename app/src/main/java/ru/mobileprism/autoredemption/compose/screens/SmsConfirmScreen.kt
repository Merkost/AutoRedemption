package ru.mobileprism.autoredemption.compose.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.viewModel
import ru.mobileprism.autoredemption.R
import ru.mobileprism.autoredemption.utils.PhoneNumberVisualTransformation

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SmsConfirmScreen(onNext: (/*PhoneRegisterEntity*/) -> Unit) {

    //val viewModel: PhoneEnteringViewModel by viewModel()
    val context = LocalContext.current

    val phone by remember {
        mutableStateOf("+79876543210")
    }
    var smsCode by remember {
        mutableStateOf("123456")
    }

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
        else viewModel.authenticate()*/
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

                Text(text = buildAnnotatedString {
                    append(
                        AnnotatedString(context.getString(R.string.sms_was_sent) + " $phone")
                    )
                    /*addStyle(
                        SpanStyle(
*//*
                            baselineShift = BaselineShift.Subscript
*//*
                        ), start = 0,
                        end = context.getString(R.string.we_send_you_sms_label).length + 1 + regEntity.phone.length
                    )*/
                    addStyle(
                        SpanStyle(
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,

                            ), start = context.getString(R.string.sms_was_sent).length + 1,
                        end = context.getString(R.string.sms_was_sent).length + 1 + phone.length
                    )
                })
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Введите его ниже", style = MaterialTheme.typography.body1)

            }

            val (textFieldCode) = remember { FocusRequester.createRefs() }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(textFieldCode),
                    value = smsCode,
                    onValueChange = { newValue: String ->
                        smsCode = newValue
                    },
                    /*readOnly = uiState is BaseViewState.Loading,*/
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
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        onReady()
                    }),
                    /*isError = viewModel.isPhoneError.value*/
                )
                TextButton(onClick = {}) {
                    Text(text = "Запросить код повторно")
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

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