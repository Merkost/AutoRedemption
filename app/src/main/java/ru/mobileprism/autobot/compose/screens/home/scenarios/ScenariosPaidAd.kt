package ru.mobileprism.autobot.compose.screens.home.scenarios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.mobileprism.autobot.compose.custom.*
import ru.mobileprism.autobot.compose.screens.home.AutoBotTextField
import ru.mobileprism.autobot.compose.screens.home.ListSpacer
import ru.mobileprism.autobot.utils.Constants

@Composable
fun ScenariosPaidAd(upPress: () -> Unit) {
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
    val checkedValues = remember {
        mutableStateListOf<Number>()
    }

    ScenariosLayout(topBar = {
        DefaultTopAppBar(title = "Поднятое сообщение", upPress = upPress)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Объединить сообщения в одно?", modifier = Modifier.weight(1f, false))
                Spacer(modifier = Modifier.size(4.dp))
                Switch(checked = shouldUnite, onCheckedChange = { shouldUnite = it })
            }

            DefaultColumn {
                Text(
                    text = "● Когда продавец авто применил платное поднятие объявления, от Вас будет отправлено сообщение."
                )
                Text(
                    text = "● Продавец может поднимать объявления сколько угодно раз. От вас сообщения могут отправляться как кажлый раз, " + "так на какие-то определеленные поднятия. Например, если продавец поднимает уже 3 раза."
                )
                Text(text = "● Отметьте, на какие поднятия отправлять сообщения?")
                CountSelectRow((1..10).toList(), checkedValues = checkedValues,
                    onNumberClick = {
                        if (it in checkedValues) checkedValues.remove(it)
                        else checkedValues.add(it)
                    }
                )
            }
            Column {
                Text(
                    text = "Через какой промежуток времени после поднятия отправлять сообщение?"
                )
                TimeSelectRow(value = minuteDelay, onValueChange = { minuteDelay = it })
            }
            SwitchRow(
                text = "Не отправлять сообщение, еспи в этот день уже было отправлено " +
                        "сообщение из другого сценария (например: повторное сообщение" +
                        "или изменение стоимости).",
                checked = shouldUnite, onCheckedChange = { shouldUnite = it }
            )

            ListSpacer()
        }

    }
}
