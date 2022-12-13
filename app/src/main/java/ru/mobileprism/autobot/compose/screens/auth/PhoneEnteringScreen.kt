package ru.mobileprism.autobot.compose.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.viewModel
import ru.mobileprism.autobot.BuildConfig
import ru.mobileprism.autobot.R
import ru.mobileprism.autobot.compose.custom.MainButton
import ru.mobileprism.autobot.compose.custom.ModalLoadingDialog
import ru.mobileprism.autobot.compose.screens.home.AutoBotTextField
import ru.mobileprism.autobot.model.datastore.CityEntity
import ru.mobileprism.autobot.model.datastore.UserEntity
import ru.mobileprism.autobot.model.entities.PhoneAuthEntity
import ru.mobileprism.autobot.model.repository.AuthRepository
import ru.mobileprism.autobot.utils.*
import ru.mobileprism.autobot.viewmodels.AuthManager
import ru.mobileprism.autobot.viewmodels.PhoneEnteringViewModel

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun PhoneEnteringScreen(onNext: (PhoneAuthEntity) -> Unit) {
    val authManager: AuthManager = get()
    val viewModel: PhoneEnteringViewModel by viewModel()
    val coroutineScope = rememberCoroutineScope()
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
                context.showError(state.autoBotError)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val onReady: () -> Unit = {
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
            .consumedWindowInsets(WindowInsets.navigationBars)
            .statusBarsPadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 30.dp)
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp
                    )
                )
                Text(text = stringResource(R.string.app_description))
            }

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AutoBotTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = phoneNum.value,
                    onValueChange = { newValue: String ->
                        viewModel.onPhoneSet(newValue)
                    },
//                    readOnly = uiState is BaseViewState.Loading,
                    leadingIcon = {
                        Image(
                            painterResource(R.drawable.russian_flag),
                            "",
                            modifier = Modifier
                                .size(25.dp)
                                .noRippleClickable(Constants.isDebug) {
                                    coroutineScope.launch {
                                        authManager.saveUserWithToken(
                                            UserEntity(
                                                name = "Test",
                                                city = CityEntity("0", "", "")
                                            ), "debug"
                                        )
                                    }
                                }
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
                        MaterialTheme.colorScheme.onSurface, fontSize = 24.sp,
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
                    isError = viewModel.isPhoneError.collectAsState().value,
                    visualTransformation = PhoneNumberVisualTransformation()
                )
            }

            MainButton(
                modifier = Modifier,
                content = { Text(text = stringResource(R.string.proceed)) },
                onClick = onReady
            )
        }

    }
}

