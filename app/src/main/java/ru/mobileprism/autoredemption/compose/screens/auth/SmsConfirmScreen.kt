package ru.mobileprism.autoredemption.compose.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
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
import ru.mobileprism.autoredemption.viewmodels.SmsVerificationViewModel
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import ru.mobileprism.autoredemption.R
import ru.mobileprism.autoredemption.model.entities.PhoneAuthEntity
import ru.mobileprism.autoredemption.utils.BaseViewState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SmsConfirmScreen(phoneAuth: PhoneAuthEntity, onNext: () -> Unit) {

    val viewModel: SmsVerificationViewModel by viewModel(parameters = { parametersOf(phoneAuth) })
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    val smsCode = viewModel.smsCode.collectAsState()
    val retrySecs = viewModel.retrySecs.collectAsState()

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
        viewModel.login()
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
                        AnnotatedString(context.getString(R.string.sms_was_sent) + " ${phoneAuth.phone}")
                    )

                    addStyle(
                        SpanStyle(
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,

                            ), start = context.getString(R.string.sms_was_sent).length + 1,
                        end = context.getString(R.string.sms_was_sent).length + 1 + phoneAuth.phone.length
                    )
                })
                Spacer(modifier = Modifier.size(16.dp))
                Text(text = "Введите его ниже", style = MaterialTheme.typography.body2)

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
                    value = smsCode.value,
                    onValueChange = viewModel::onSmsCodeValueChange,
                    readOnly = uiState is BaseViewState.Loading,
                    singleLine = true,
                    trailingIcon = {
                        AnimatedVisibility(
                            smsCode.value.isNotEmpty(),
                            enter = fadeIn(), exit = fadeOut()
                        ) {
                            IconButton(onClick = viewModel::resetSmsCode) {
                                Icon(Icons.Default.Close, Icons.Default.Close.name)
                            }
                        }
                    },
                    textStyle = TextStyle(
                        MaterialTheme.colors.onSurface, fontSize = 24.sp,
                        textAlign = TextAlign.Start, fontWeight = FontWeight.SemiBold,
                        letterSpacing = 4.sp,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        onReady()
                    }),
                    isError = viewModel.isError.value
                )
                Crossfade(targetState = retrySecs.value) { secs ->
                    when(secs) {
                        0 -> {
                            TextButton(onClick = viewModel::retry) {
                                Text(text = "Запросить код повторно")
                            }
                        }
                        else -> {
                            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                                Text(text = "Запросить код повторно можно через: ${secs ?: ""}")
                            }
                        }
                    }
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