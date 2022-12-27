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
import ru.mobileprism.autobot.R
import ru.mobileprism.autobot.compose.custom.*
import ru.mobileprism.autobot.compose.screens.home.AutoBotTextField
import ru.mobileprism.autobot.compose.screens.home.ListSpacer
import ru.mobileprism.autobot.utils.Constants

@Composable
fun ScenariosFirstMessage(upPress: () -> Unit) {

    var firstMessage by remember {
        mutableStateOf("Здравствуйте!")
    }
    var secondMessage by remember {
        mutableStateOf(
            "Интересует ваш автомобиль, " + "какие дефекты присутствуют? " + "Готов рассмотреть для покупки " + "сегодня, если договоримся по цене."
        )
    }
    var shouldUnite by remember {
        mutableStateOf(false)
    }
    var minuteDelay by remember {
        mutableStateOf("")
    }

    ScenariosLayout(topBar = {
        DefaultTopAppBar(title = "Первое сообщение", upPress = upPress)
    }, onSaveClick = {}) {
        ScenariosColumn(
            modifier = Modifier
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Текст приветствия - этот текст будет отправлен первым сообщением.")
                AutoBotTextField(placeholder = "Текст приветствия",
                    modifier = Modifier.fillMaxWidth(),
                    value = firstMessage,
                    onValueChange = { firstMessage = it })
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Основной текст - это сообщение будет" + " отправлено сразу после текста приветствия."
                )
                AutoBotTextField(placeholder = "Основной текст",
                    modifier = Modifier.fillMaxWidth(),
                    value = secondMessage,
                    onValueChange = { secondMessage = it })
            }
            SwitchRow(
                text = "Объединить сообщения в одно?",
                checked = shouldUnite,
                onCheckedChange = { shouldUnite = it }
            )

            Column {
                Text(
                    text = "Когда появилось интересующее вас объявление, " + "через сколько времени отправлять его владельцу " + "сообщение с этим текстом"
                )
                TimeSelectRow(value = minuteDelay, onValueChange = { minuteDelay = it })
            }
            ListSpacer()
        }

    }
}


