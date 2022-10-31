package ru.mobileprism.autoredemption.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.mobileprism.autoredemption.MainActivityViewModel
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.datastore.AppSettingsImpl
import ru.mobileprism.autoredemption.model.datastore.UserDatastore
import ru.mobileprism.autoredemption.model.datastore.UserDatastoreImpl
import ru.mobileprism.autoredemption.model.repository.AuthRepository
import ru.mobileprism.autoredemption.model.repository.AuthRepositoryImpl
import ru.mobileprism.autoredemption.utils.CurrentUserHandler
import ru.mobileprism.autoredemption.viewmodels.*

val koinAppModule = module {

    single<AppSettings> { AppSettingsImpl(androidContext()) }
    single<UserDatastore> { UserDatastoreImpl(androidContext()) }


    single<AuthRepository> { AuthRepositoryImpl(get()) }

    single<AuthManager> { AuthManagerImpl(userDatastore = get()) }

    single { CurrentUserHandler(userDatastore = get()) }

    viewModel {
        MainActivityViewModel(userDatastore = get())
    }

    viewModel {
        ProfileViewModel(authManager = get())
    }

    viewModel {
        HomeViewModel(appSettings = get())
    }

    viewModel { PhoneEnteringViewModel(authRepository = get(), authManager = get()) }
    viewModel {
        SmsVerificationViewModel(
            authManager = get(),
            authRepository = get(),
            phoneAuth = get()
        )
    }


}


