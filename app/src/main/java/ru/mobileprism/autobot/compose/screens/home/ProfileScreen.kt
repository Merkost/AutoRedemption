package ru.mobileprism.autobot.compose.screens.home

import android.view.ViewTreeObserver
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import ru.mobileprism.autobot.compose.custom.CircleButton
import ru.mobileprism.autobot.utils.Constants.DAY_MONTH_YEAR_TIME
import ru.mobileprism.autobot.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(upPress: () -> Unit, toAuth: () -> Unit) {
    val viewModel: ProfileViewModel = getViewModel()

    val user = viewModel.currentUser.collectAsState()

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
                .padding(30.dp)
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AutoBotTextField(placeholder = "Телефон", value = user.value.phone, onValueChange = {})
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
                Text(
                    text = "Город: $it"
                )
            }

            user.value.timeZone?.label?.let {
                Text(
                    text = "Часовой пояс: $it"
                )
            }


//            Text(text = user.value.toString())

            CircleButton(
                onClick = { viewModel.logout(); toAuth() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Выйти из аккаунта")
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AutoBotTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape =
        MaterialTheme.shapes.small.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors()
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    val scope = rememberCoroutineScope()
    val view = LocalView.current
    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            scope.launch { bringIntoViewRequester.bringIntoView() }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose { view.viewTreeObserver.removeOnGlobalLayoutListener(listener) }
    }

    OutlinedTextField(
        modifier = modifier.bringIntoViewRequester(bringIntoViewRequester),
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
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
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