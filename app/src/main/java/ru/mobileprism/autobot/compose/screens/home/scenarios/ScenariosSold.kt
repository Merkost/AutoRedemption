package ru.mobileprism.autobot.compose.screens.home.scenarios

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.getViewModel
import ru.mobileprism.autobot.R
import ru.mobileprism.autobot.compose.custom.*
import ru.mobileprism.autobot.compose.screens.home.ListSpacer
import ru.mobileprism.autobot.viewmodels.scenarios.SoldViewModel

@Composable
fun ScenariosSold(upPress: () -> Unit) {
    val viewModel: SoldViewModel = getViewModel()

    val values by viewModel.values.collectAsState()

    ScenariosLayout(
        topBar = {
            DefaultTopAppBar(title = stringResource(R.string.scenarios_sold), upPress = upPress)
        }, onSaveClick = {}
    ) {
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

            Column {
                Text(text = "Через сколько дней написать продавцу собошение после того, как eго объявление было отмечено проданным")
                TimeSelectRow(
                    value = values.daysAfter.toString(),
                    onValueChange = viewModel::afterDaysChanged,
                    timeAmount = stringResource(id = R.string.days_after)
                )
            }
            ListSpacer()
        }

    }
}

val defaultSoldModel = SoldModel(
    helloText = "Здравствуйте!",
    text = "Знакомый сказал, что вы купили новый автомобиль и вам будет интересна оклейка автомобиля бронировочной плёнкой: " +
            "Если интересно, можем созвониться когда будет удобно.",
    daysAfter = 1
)

data class SoldModel(
    var helloText: String = "",
    var text: String = "",
    var daysAfter: Int = 3,
)