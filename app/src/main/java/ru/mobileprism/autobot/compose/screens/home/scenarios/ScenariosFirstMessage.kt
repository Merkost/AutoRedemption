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
import ru.mobileprism.autobot.compose.custom.DefaultColumn
import ru.mobileprism.autobot.compose.screens.home.AutoBotTextField
import ru.mobileprism.autobot.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenariosFirstMessage(upPress: () -> Unit) {

    var firstMessage by remember {
        mutableStateOf("Здравствуйте!")
    }
    var secondMessage by remember {
        mutableStateOf(
            "Интересует ваш автомобиль, \n" +
                    "какие дефекты присутствуют? \n" +
                    "Готов рассмотреть для покупки \n" +
                    "сегодня, если договоримся по цене."
        )
    }
    var shouldUnite by remember {
        mutableStateOf(false)
    }
    var minuteDelay by remember {
        mutableStateOf("")
    }

    Scaffold(topBar = {
        DefaultTopAppBar(title = "Первое сообщение", upPress = upPress)
    }, contentWindowInsets = WindowInsets(0.dp)) {
        DefaultColumn(
            modifier = Modifier
                .padding(it)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(Constants.defaultPadding)



        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Текст приветствия - этот текст будет отправлен первым сообщением.")
                AutoBotTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = firstMessage,
                    onValueChange = { firstMessage = it })
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Основной текст - это сообщение будет" +
                            "отправлено сразу после текста приветствия."
                )
                AutoBotTextField(
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
                    text = "Когда появилось интересующее вас объявление, " +
                            "через сколько времени отправлять его владельцу " +
                            "сообщение с этим текстом"
                )
                TimeSelectRow(
                    value = minuteDelay,
                    onValueChange = { minuteDelay = it })
            }
        }
    }
}

@Composable
fun TimeSelectRow(
    value: String,
    modifier: Modifier = Modifier,
    timeAmount: String = stringResource(id = R.string.minute_after),
    onValueChange: (String) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
    ) {
        Text("Через", modifier = Modifier)
        AutoBotTextField(modifier = Modifier
            .wrapContentWidth()
            .weight(1f, false), value = value, onValueChange = onValueChange)
        Text(text = timeAmount, modifier = Modifier.weight(1f, true))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(title: String, upPress: (() -> Unit)? = null) {
    TopAppBar(title = {
        Text(text = ("Первое сообщение"))
    }, navigationIcon = {
        upPress?.let {
            IconButton(onClick = upPress) {
                Icon(Icons.Default.ArrowBack, Icons.Default.ArrowBack.name)
            }
        }
    })
}
