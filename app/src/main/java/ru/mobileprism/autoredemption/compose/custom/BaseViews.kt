package ru.mobileprism.autoredemption.compose.custom

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.mobileprism.autoredemption.R

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
            modifier = Modifier.fillMaxWidth(),
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
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(12.dp),
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