package ru.mobileprism.autobot.compose.custom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.mobileprism.autobot.R
import ru.mobileprism.autobot.compose.screens.home.AutoBotTextField
import ru.mobileprism.autobot.compose.screens.home.ListSpacer
import ru.mobileprism.autobot.compose.screens.home.scenarios.DefaultTopAppBar
import ru.mobileprism.autobot.compose.screens.home.scenarios.TimeSelectRow
import ru.mobileprism.autobot.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenariosLayout(topBar: @Composable () -> Unit, onSaveClick: () -> Unit, layout: @Composable () -> Unit) {
    Scaffold(topBar = topBar, contentWindowInsets = WindowInsets(0.dp)) {
        Box(modifier = Modifier
            .padding(horizontal = Constants.defaultPadding)
            .padding(vertical = Constants.smallPadding)
        ) {
            DefaultColumn(
                modifier = Modifier
                    .padding(it)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                layout()
            }
            MainButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                content = { Text(text = stringResource(R.string.save)) },
                onClick = onSaveClick
            )
        }

    }
}