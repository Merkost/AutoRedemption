package ru.mobileprism.autobot.compose.screens.home.scenarios

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.mobileprism.autobot.compose.custom.ScenariosLayout
import ru.mobileprism.autobot.compose.screens.home.AutoBotTextField
import ru.mobileprism.autobot.compose.screens.home.ListSpacer

@Composable
fun ScenariosOtherMessages(upPress: () -> Unit) {

//    val pagerState = rememberPagerState
    var firstMessage by remember {
        mutableStateOf("Добрый день!")
    }
    var secondMessage by remember {
        mutableStateOf(
            "Наблюдаю за вашим авто, может" +
                    "быть встретимся, посмотрим его," +
                    "поговорим о цене ? Что скажете ?"
        )
    }
    var shouldUnite by remember {
        mutableStateOf(false)
    }
    var minuteDelay by remember {
        mutableStateOf("")
    }

    ScenariosLayout(
        topBar = {
            DefaultTopAppBar(title = "Повторные сообщения", upPress = upPress)
        },
        onSaveClick = {}
    ) {

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Это сообщение отправляется через несколько дней после отправки первого сообщения")
            AutoBotTextField(
                placeholder = "Текст приветствия",
                modifier = Modifier.fillMaxWidth(),
                value = firstMessage,
                onValueChange = { firstMessage = it }
            )
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            AutoBotTextField(
                placeholder = "Основной текст",
                modifier = Modifier.fillMaxWidth(),
                value = secondMessage,
                onValueChange = { secondMessage = it })
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Объединить сообщения в одно?")
            Spacer(modifier = Modifier.size(4.dp))
            Switch(checked = shouldUnite, onCheckedChange = { shouldUnite = it })
        }

        Column {
            Text(
                text = "Через сколько дней после" +
                        "отправки первого сообщения, нужно" +
                        "отправить это сообщение?"
            )
            TimeSelectRow(value = minuteDelay, onValueChange = { minuteDelay = it }, timeAmount = "дней")
        }
        ListSpacer()

    }
}