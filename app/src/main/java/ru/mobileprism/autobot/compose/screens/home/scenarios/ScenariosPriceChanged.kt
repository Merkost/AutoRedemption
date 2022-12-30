package ru.mobileprism.autobot.compose.screens.home.scenarios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.update
import org.koin.androidx.compose.getViewModel
import ru.mobileprism.autobot.R
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
        DefaultTopAppBar(title = stringResource(R.string.price_changing), upPress = upPress)
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

            DefaultColumn {
                Text(
                    text = "● Продавец может изменять стоимость автомобиля. Если стоимость изменилась " +
                            "в меньшую сторону, тогда человеку будет отправлено сообщение"
                )
                Text(
                    text = "● Продавец может много раз в день изменить стоимость. Вы можете отправлять сообщение" +
                            " на каждое изменение или только 1 раз в день. "
                )
            }

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
                TimeSelectRow(
                    value = values.minutesAfter.toString(),
                    onValueChange = viewModel::afterMinutesChanged
                )
            }
            ListSpacer()
        }

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

