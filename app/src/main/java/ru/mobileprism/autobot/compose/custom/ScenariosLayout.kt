package ru.mobileprism.autobot.compose.custom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.mobileprism.autobot.R
import ru.mobileprism.autobot.compose.screens.home.AutoBotTextField
import ru.mobileprism.autobot.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenariosLayout(topBar: @Composable () -> Unit, onSaveClick: () -> Unit, layout: @Composable () -> Unit) {
    Scaffold(topBar = topBar, contentWindowInsets = WindowInsets(0.dp)) {
        Box(modifier = Modifier
            .padding(it)
            .padding(vertical = Constants.smallPadding)
        ) {
            layout()
            MainButton(
                modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = Constants.defaultPadding),
                content = { Text(text = stringResource(R.string.save)) },
                onClick = onSaveClick
            )
        }

    }
}

@Composable
fun CountSelectRow(
    values: List<Number>, checkedValues: List<Number>, onNumberClick: (Number) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Constants.smallPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(values) { value ->
            CountItem(
                number = value,
                checked = value in checkedValues,
                onClick = { onNumberClick(value) })
        }
    }
}

@Composable
fun CountItem(number: Number, checked: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Checkbox(checked = checked, onCheckedChange = { onClick() })
        Text(text = number.toString())
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
        AutoBotTextField(
            modifier = Modifier
                .wrapContentWidth()
                .weight(1f, false),
            value = value,
            onValueChange = onValueChange,
            singleLine = true
        )
        Text(text = timeAmount, modifier = Modifier.weight(1f, true))
    }
}