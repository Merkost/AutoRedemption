package ru.mobileprism.autoredemption.compose.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.get
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.datastore.UserEntity

@Composable
fun ProfileScreen(upPress: () -> Unit) {
    val appSettings: AppSettings = get()

    val user = appSettings.getCurrentUserNullable.collectAsState(null)

    Scaffold {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = user.value.toString())
        }
    }

}