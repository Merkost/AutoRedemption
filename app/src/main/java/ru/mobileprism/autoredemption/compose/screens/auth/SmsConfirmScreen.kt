package ru.mobileprism.autoredemption.compose.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.mobileprism.autoredemption.viewmodels.SmsVerificationViewModel
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import ru.mobileprism.autoredemption.ConfirmSmsMutation
import ru.mobileprism.autoredemption.R
import ru.mobileprism.autoredemption.compose.custom.MainButton
import ru.mobileprism.autoredemption.compose.custom.ModalLoadingDialog
import ru.mobileprism.autoredemption.model.entities.PhoneAuthEntity
import ru.mobileprism.autoredemption.model.entities.SmsConfirmEntity
import ru.mobileprism.autoredemption.type.Date
import ru.mobileprism.autoredemption.utils.BaseViewState
import ru.mobileprism.autoredemption.utils.showError

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SmsConfirmScreen(
    phoneAuth: PhoneAuthEntity,
    upPress: () -> Unit,
    onNext: (SmsConfirmEntity) -> Unit
) {

    val viewModel: SmsVerificationViewModel by viewModel(parameters = { parametersOf(phoneAuth) })
    val context = LocalContext.current

    val uiState = viewModel.uiState.collectAsState()
    val smsCode = viewModel.smsCode.collectAsState()
    val retrySecs = viewModel.retrySecs.collectAsState()

    ModalLoadingDialog(
        onDismiss = { viewModel.cancelLoading() },
        isLoading = uiState.value is BaseViewState.Loading
    )

    LaunchedEffect(uiState.value) {
        when (val state = uiState.value) {
            is BaseViewState.Success -> {
                onNext(state.data)
                viewModel.resetState()
            }
            is BaseViewState.Error -> {
                context.showError(state.autoBotError)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            AuthTopAppBar(upPress = upPress)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = buildAnnotatedString {
                        append(
                            AnnotatedString(context.getString(R.string.sms_was_sent) + " ${phoneAuth.phone}")
                        )

                        addStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,

                                ), start = context.getString(R.string.sms_was_sent).length + 1,
                            end = context.getString(R.string.sms_was_sent).length + 1 + phoneAuth.phone.length
                        )
                    },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp
                    ),
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(text = "Введите его ниже", style = MaterialTheme.typography.titleMedium)

            }

            val (textFieldCode) = remember { FocusRequester.createRefs() }

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(textFieldCode),
                    value = smsCode.value,
                    onValueChange = viewModel::onSmsCodeValueChange,
//                    readOnly = uiState.value is BaseViewState.Loading,
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
                        MaterialTheme.colorScheme.onSurface, fontSize = 24.sp,
                        textAlign = TextAlign.Start, fontWeight = FontWeight.SemiBold
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.login()

                    }),
                    isError = viewModel.isError.value
                )
                when (retrySecs.value) {
                    0 -> {
                        TextButton(onClick = viewModel::retry) {
                            Text(
                                text = "Запросить код повторно",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    else -> {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                            Text(
                                text = "Повторный запрос (${retrySecs.value})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

            }
            Column(
                modifier = Modifier.weight(1f, false),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainButton(
                    modifier = Modifier,
                    content = {
                        Text(text = "Продолжить")
                    },
                    onClick = viewModel::login

                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTopAppBar(title: String? = null, upPress: (() -> Unit)? = null, actions: @Composable RowScope.() -> Unit = {}) {
    TopAppBar(
        title = {
                title?.let {
                    Text(text = title)
                }
        },
        navigationIcon = {
            upPress?.let {
                IconButton(onClick = upPress) {
                    Icon(Icons.Default.ArrowBack, "")
                }
            }
        },
//        elevation = 0.dp,
//        backgroundColor = Color.Transparent
        actions = actions
    )
}
