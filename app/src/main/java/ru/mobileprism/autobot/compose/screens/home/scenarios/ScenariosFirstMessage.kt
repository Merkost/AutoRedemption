package ru.mobileprism.autobot.compose.screens.home.scenarios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import ru.mobileprism.autobot.R
import ru.mobileprism.autobot.compose.custom.*
import ru.mobileprism.autobot.compose.screens.home.AutoBotTextField
import ru.mobileprism.autobot.compose.screens.home.ListSpacer
import ru.mobileprism.autobot.utils.Constants
import ru.mobileprism.autobot.viewmodels.scenarios.FirstMessageViewModel

@Composable
fun ScenariosFirstMessage(upPress: () -> Unit) {

    val viewModel: FirstMessageViewModel = getViewModel()
    val values by viewModel.values.collectAsState()

    ScenariosLayout(topBar = {
        DefaultTopAppBar(title = stringResource(R.string.first_message), upPress = upPress)
    }, onSaveClick = {}) {
        ScenariosColumn(
            modifier = Modifier
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            ScenariosTextField(
                title = stringResource(id = R.string.hello_text_description),
                placeholder = stringResource(id = R.string.hello_text_placeholder),
                value = values.helloText,
                onValueChange = viewModel::onHelloTextChanged
            )

            ScenariosTextField(
                title = stringResource(id = R.string.main_text_description),
                placeholder = stringResource(id = R.string.main_text_placeholder),
                value = values.text,
                onValueChange = viewModel::onPrimaryTextChanged
            )

            SwitchRow(
                text = stringResource(R.string.unite_messages_question),
                checked = values.shouldUnite,
                onCheckedChange = viewModel::onShouldUniteChanged
            )

            Column {
                Text(text = "Когда появилось интересующее вас объявление, через сколько времени отправлять его владельцу сообщение с этим текстом")
                TimeSelectRow(value = values.minutesAfter.toString(), onValueChange = viewModel::afterDaysChanged)
            }
            ListSpacer()
        }

    }
}

val defaultFirstMessageModel = FirstMessageModel(
    helloText = "Здравствуйте!",
    text = "Интересует ваш автомобиль, какие дефекты присутствуют? " +
            "Готов рассмотреть для покупки сегодня, если договоримся по цене.",
    minutesAfter = 1
)

data class FirstMessageModel(
    var helloText: String = "",
    var text: String = "",
    val shouldUnite: Boolean = false,
    var minutesAfter: Int = 10,
)


