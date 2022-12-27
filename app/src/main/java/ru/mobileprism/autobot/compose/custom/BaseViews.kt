package ru.mobileprism.autobot.compose.custom

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.mobileprism.autobot.R
import ru.mobileprism.autobot.utils.Constants


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(
    title: String, upPress: (() -> Unit)? = null, actions: @Composable() (RowScope.() -> Unit) = {}
) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            upPress?.let {
                IconButton(onClick = upPress) {
                    Icon(Icons.Default.ArrowBack, Icons.Default.ArrowBack.name)
                }
            }
        },
        actions = actions
    )
}

@Composable
fun SmallErrorViewVertical(
    text: String = stringResource(id = R.string.unable_reach_server),
    modifier: Modifier = Modifier,
    retryText: String = stringResource(id = R.string.reload),
    onRetry: (() -> Unit)? = null,
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            modifier = Modifier,
            style = MaterialTheme.typography.titleMedium
        )
        onRetry?.let {
            Spacer(modifier = Modifier.size(12.dp))
            CircleButton(onClick = onRetry) {
                Text(retryText)
            }
        }

    }
}

@Composable
fun DefaultColumn(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(16.dp),
    function: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {
        function()
    }
}

@Composable
fun ScenariosColumn(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(32.dp),
    function: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = Constants.defaultPadding),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {
        function()
    }
}