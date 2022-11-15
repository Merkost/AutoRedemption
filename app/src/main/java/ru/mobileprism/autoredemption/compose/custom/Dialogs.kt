package ru.mobileprism.autoredemption.compose.custom

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension.Companion.fillToConstraints
import ru.mobileprism.autoredemption.R

@Composable
fun ModalLoadingDialog(
    isLoading: Boolean,
    text: String = stringResource(id = R.string.loading),
    onDismiss: (() -> Unit)? = null
) {
    if (isLoading) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = text,
                        color = Color.White
                    )
                }
                onDismiss?.let {
                    CircleButton(onClick = { onDismiss() }) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            }
        }
    }
}


@Composable
fun DefaultDialog(
    primaryText: String? = null,
    textAlign: TextAlign = TextAlign.Start,
    primaryTextWeight: FontWeight = FontWeight.SemiBold,
    secondaryText: String? = null,
    neutralButtonText: String = "",
    onNeutralClick: (() -> Unit)? = null,
    negativeButtonText: String = stringResource(id = R.string.no),
    onNegativeClick: (() -> Unit)? = null,
    positiveButtonText: String = stringResource(id = R.string.yes),
    onPositiveClick: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    content: @Composable() (() -> Unit)? = null
) {

    val anyButtonAvailable =
        onNeutralClick != null || onNegativeClick != null || onPositiveClick != null

    val textBias = when (textAlign) {
        TextAlign.Start -> 0f
        TextAlign.End -> 1f
        else -> 0.5f
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .animateContentSize()
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = if (anyButtonAvailable) 4.dp else 0.dp)
            ) {
                val (title, subtitle, mainContent, neutralButton, negativeButton, positiveButton) = createRefs()

                primaryText?.let {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        val textStyle =
                            MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        ProvideTextStyle(textStyle) {
                            Text(
                                modifier = Modifier.constrainAs(title) {
                                    top.linkTo(parent.top, 16.dp)
                                    linkTo(
                                        parent.absoluteLeft,
                                        parent.absoluteRight,
                                        16.dp,
                                        16.dp,
                                        bias = textBias
                                    )
                                    width = fillToConstraints
                                },
                                text = primaryText,
                                fontWeight = primaryTextWeight,
                            )
                        }
                    }

                }

                if (secondaryText != null) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.medium
                    ) {
                        val textStyle = MaterialTheme.typography.bodyMedium
                        ProvideTextStyle(textStyle) {
                            Text(
                                modifier = Modifier.constrainAs(subtitle) {
                                    top.linkTo(title.bottom, 8.dp)
                                    linkTo(
                                        parent.absoluteLeft,
                                        parent.absoluteRight,
                                        16.dp,
                                        16.dp,
                                        bias = textBias
                                    )
                                    width = fillToConstraints
                                },
                                text = secondaryText
                            )
                        }
                    }

                } else {
                    Spacer(modifier = Modifier
                        .size(0.dp)
                        .constrainAs(subtitle) {
                            top.linkTo(title.bottom, 2.dp)
                            absoluteLeft.linkTo(title.absoluteLeft)
                        })
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .constrainAs(mainContent) {
                            top.linkTo(subtitle.bottom, 14.dp)
                            absoluteLeft.linkTo(parent.absoluteLeft)
                            absoluteRight.linkTo(parent.absoluteRight)
                            width = fillToConstraints
                        },
                    contentAlignment = Alignment.Center
                ) {
                    content?.invoke()
                }

                if (onNeutralClick != null) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.disabled
                    ) {
                        TextButton(
                            modifier = Modifier.constrainAs(neutralButton) {
                                top.linkTo(mainContent.bottom, 16.dp)
                                absoluteLeft.linkTo(parent.absoluteLeft)
                            },
                            content = { Text(text = neutralButtonText) },
                            onClick = onNeutralClick,
                        )
                    }
                }

                if (onPositiveClick != null) {
                    Button(
                        modifier = Modifier.constrainAs(positiveButton) {
                            top.linkTo(mainContent.bottom, 16.dp)
                            bottom.linkTo(parent.bottom)
                            absoluteRight.linkTo(parent.absoluteRight, 12.dp)
                        },
                        content = { Text(text = positiveButtonText) },
                        onClick = onPositiveClick,
                    )
                } else {
                    Spacer(
                        modifier = Modifier
                            .size(0.dp)
                            .constrainAs(positiveButton) {
                                top.linkTo(mainContent.bottom, 16.dp)
                                bottom.linkTo(parent.bottom)
                                absoluteRight.linkTo(parent.absoluteRight)
                            },
                    )
                }

                if (onNegativeClick != null) {
                    TextButton(
                        modifier = Modifier.constrainAs(negativeButton) {
                            top.linkTo(mainContent.bottom, 16.dp)
                            absoluteRight.linkTo(positiveButton.absoluteLeft, 12.dp)
                        },
                        content = { Text(text = negativeButtonText) },
                        onClick = onNegativeClick,
                    )
                }
            }
        }
    }
}
