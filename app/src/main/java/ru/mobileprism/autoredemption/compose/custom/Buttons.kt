package ru.mobileprism.autoredemption.compose.custom

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
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
fun MainButton(modifier: Modifier, content: @Composable RowScope.() -> Unit, onClick: () -> Unit) {
    Button(
        modifier = modifier.fillMaxWidth().heightIn(45.dp),
        content = content,
        onClick = onClick
    )
}
