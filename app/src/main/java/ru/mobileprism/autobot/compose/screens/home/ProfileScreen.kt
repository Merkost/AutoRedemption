package ru.mobileprism.autobot.compose.screens.home

import android.view.ViewTreeObserver
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import ru.mobileprism.autobot.R
import ru.mobileprism.autobot.compose.custom.CircleButton
import ru.mobileprism.autobot.compose.custom.DefaultAlertDialog3
import ru.mobileprism.autobot.utils.Constants
import ru.mobileprism.autobot.utils.Constants.DAY_MONTH_YEAR_TIME
import ru.mobileprism.autobot.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(upPress: () -> Unit, toAuth: () -> Unit) {
    val viewModel: ProfileViewModel = getViewModel()
    val user = viewModel.currentUser.collectAsState()
    var logoutDialog by remember { mutableStateOf(false) }

    if (logoutDialog)
        DefaultAlertDialog3(
            onDismiss = { logoutDialog = false },
            primaryText = stringResource(R.string.logout_dialog_title),
            secondaryText = stringResource(R.string.logout_dialog_message),
            positiveButtonText = stringResource(R.string.exit_label),
            onPositiveClick = {
                viewModel.logout(); toAuth()
                logoutDialog = false
            },
            dismissButtonText = stringResource(id = R.string.cancel),
            onDismissClick = {
                logoutDialog = false
            },
        )

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Профиль") },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(Constants.defaultPadding)
                .padding(it).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AutoBotTextField(placeholder = "Телефон", value = user.value.phone, onValueChange = {}, readOnly = true)
            Text(
                text = "Зарегистрирован: ${
                    user.value.createdAt.format(
                        DAY_MONTH_YEAR_TIME
                    )
                }"
            )
            user.value.subscriptionStatus.let {
                if (it.isActive) {
                    Text(
                        text = "Подписка активна до: ${
                            it.subscriptionEnds.format(
                                DAY_MONTH_YEAR_TIME
                            )
                        }"
                    )
                } else {
                    Text(text = "Подписка неактивна")
                }
            }

            user.value.city?.label?.let {
                Text(text = "Город: $it")
            }

            user.value.timeZone?.label?.let {
                Text(text = "Часовой пояс: $it")
            }

            CircleButton(
                onClick = { logoutDialog = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Выйти из аккаунта")
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun AutoBotTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions? = null,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    shape: Shape =
        MaterialTheme.shapes.small.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors()
) {
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val interactionSourceState = interactionSource.collectIsFocusedAsState()
    val scope = rememberCoroutineScope()
    val isImeVisible = WindowInsets.isImeVisible


    // Bring the composable into view (visible to user).
    LaunchedEffect(isImeVisible, interactionSourceState.value) {
        if (isImeVisible && interactionSourceState.value) {
            scope.launch {
                delay(300)
                bringIntoViewRequester.bringIntoView()
            }
        }
    }

    val focusRequester = FocusRequester()
    val isFocused = remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier
            .focusRequester(focusRequester)
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusChanged {
                isFocused.value = it.isFocused
            }
            .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(12.dp),
        maxLines = maxLines,
        label = {
            placeholder?.let {
                Text(
                    text = it,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = maxLines,
                )
            }
        },
        keyboardActions = keyboardActions ?: KeyboardActions(
            onDone = { focusManager.clearFocus() },
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onSearch = { focusManager.clearFocus() }
        ),
        keyboardOptions = keyboardOptions,
        enabled = enabled,
        singleLine = singleLine,
        readOnly = readOnly,
        isError = isError,
        visualTransformation = visualTransformation,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        interactionSource = interactionSource,
        colors = colors,
        textStyle = textStyle,
    )
}