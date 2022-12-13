package ru.mobileprism.autobot.compose.custom

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CircleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    enabled: Boolean = true,
    function: @Composable () -> Unit
) = OutlinedButton(
    enabled = enabled,
    onClick = onClick,
    modifier = modifier,
    shape = CircleShape,
    colors = colors
) { function() }

@Composable
fun MainButton(
    modifier: Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(45.dp),
        enabled = enabled,
        content = content,
        onClick = onClick
    )
}
