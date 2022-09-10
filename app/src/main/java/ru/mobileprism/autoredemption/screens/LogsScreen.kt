package ru.mobileprism.autoredemption.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.crazylegend.crashyreporter.CrashyReporter

@Composable
fun LogsScreen(upPress: () -> Unit) {

    val logs = remember { CrashyReporter.getLogsAsStringsAndPurge() }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "Логи")
        }, navigationIcon = {
            IconButton(onClick = upPress) {
                Icon(Icons.Default.ArrowBack, Icons.Default.ArrowBack.name)
            }
        })
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            SelectionContainer {
                logs?.forEach {
                    Text(text = it)

                }
            }

        }
    }
}
