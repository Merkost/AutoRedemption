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
import ru.mobileprism.autobot.viewmodels.scenarios.ArchivedViewModel

@Composable
fun ScenariosArchived(upPress: () -> Unit) {
    val viewModel: ArchivedViewModel = getViewModel()

    val values by viewModel.values.collectAsState()

    ScenariosLayout(
        topBar = {
            DefaultTopAppBar(title = stringResource(R.string.scenarios_archive), upPress = upPress)
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
                Text(text = "Через сколько дней написать продавцу собощение после того, как eго объявление попало в архив?")
                TimeSelectRow(value = values.daysAfter.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { viewModel.afterMinutesChanged(it) }
                    }
                )
            }

            ListSpacer()
        }

    }
}

val defaultArchiveModel = ArchiveModel(
    helloText = "Здравствуйте!",
    text = "Еще продаете автомобиль? Можем встретиться, покажете его. Что скажете?",
    daysAfter = 1
)

data class ArchiveModel(
    var helloText: String = "",
    var text: String = "",
    var daysAfter: Int = 3,
)