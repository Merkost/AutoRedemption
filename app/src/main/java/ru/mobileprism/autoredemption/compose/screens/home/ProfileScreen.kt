package ru.mobileprism.autoredemption.compose.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import ru.mobileprism.autoredemption.compose.custom.CircleButton
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.datastore.UserEntity
import ru.mobileprism.autoredemption.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(upPress: () -> Unit) {
    val profileViewModel: ProfileViewModel = getViewModel()

    val user = remember {
        mutableStateOf(profileViewModel.currentUser)
    }

    Scaffold {
        Column(
            modifier = Modifier
                .padding(30.dp)
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Телефон: ${user.value.phone}")
            Text(text = "Зарегистрирован: ${user.value.createdAt}")
            Text(text = "Подписка: ${user.value.subscriptionStatus}")



            Text(text = user.value.toString())

            CircleButton(onClick = { profileViewModel.logout() }) {
                Text(text = "Выйти из аккаунта")
            }
        }
    }

}