package ru.mobileprism.autobot.compose.screens.home.scenarios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.update
import org.koin.androidx.compose.getViewModel
import ru.mobileprism.autobot.compose.custom.*
import ru.mobileprism.autobot.compose.screens.home.AutoBotTextField
import ru.mobileprism.autobot.compose.screens.home.ListSpacer
import ru.mobileprism.autobot.utils.Constants
import ru.mobileprism.autobot.viewmodels.scenarios.PriceChangedViewModel

@Composable
fun ScenariosPriceChanged(upPress: () -> Unit) {
    val viewModel: PriceChangedViewModel = getViewModel()

    val values by viewModel.values.collectAsState()

    ScenariosLayout(topBar = {
        DefaultTopAppBar(title = "Изменение цены", upPress = upPress)
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
                    value = values.helloText,
                    onValueChange = viewModel::onHelloTextChanged )
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Основной текст - это сообщение будет" + " отправлено сразу после текста приветствия."
                )
                AutoBotTextField(placeholder = "Основной текст",
                    modifier = Modifier.fillMaxWidth(),
                    value = values.text,
                    onValueChange = viewModel::onPrimaryTextChanged)
            }
            Text(
                text = "Продавец может изменять стоимость автомобиля. Если стоимость изменилась\n" +
                        "в меньшую сторону, тогда человеку будет отправлено сообщение"
            )
            Text(
                text = "Продавец может много раз в день изменить стоимость. Вы можете отправлять сообщение" +
                        " на каждое изменение или только 1 раз в день. "
            )
            SwitchRow(
                text = "Отправлять сообщение 1 раз в день (если убрать галочку, сообщения будут уходить каждый раз при изменении стоимости",
                checked = values.onePerDay,
                onCheckedChange = viewModel::onePerDayChanged
            )
            SwitchRow(
                text = "Не отправлять сообшение, если в этот день уже быпо отправлено сообщение из другого сценария (например, повторное сообщение или поднятое объявление).",
                checked = values.dontSendIfOthersActive,
                onCheckedChange = viewModel::otherScenariosChanged
            )


            Column {
                Text(text = "Через какой промежуток времени после поднятия отправить сообщение?")
                TimeSelectRow(value = values.minutesAfter.toString(), onValueChange = {
                    it.toIntOrNull()?.let {
                        viewModel.afterMinutesChanged(it)
                    }
                })
            }
            ListSpacer()
        }

    }
}

@Composable
fun SwitchRow(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = text, modifier = Modifier.weight(1f, false))
        Spacer(modifier = Modifier.size(4.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

val defaultPriceChangedModel = PriceChangedModel(
    helloText = "Здравствуйте!",
    text = "Смотрю вы немного изменили цену авто, может быть договоримся?\n" +
            "Мне интересен ваш автомобиль",
    onePerDay = true,
    dontSendIfOthersActive = false,
    minutesAfter = 1
)

data class PriceChangedModel(
    var helloText: String = "",
    var text: String = "",
    var onePerDay: Boolean = true,
    var dontSendIfOthersActive: Boolean = false,
    var minutesAfter: Int = 5,
)

