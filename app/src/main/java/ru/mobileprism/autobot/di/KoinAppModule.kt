package ru.mobileprism.autobot.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.mobileprism.autobot.MainActivityViewModel
import ru.mobileprism.autobot.model.datastore.AppSettings
import ru.mobileprism.autobot.model.datastore.AppSettingsImpl
import ru.mobileprism.autobot.model.datastore.UserDatastore
import ru.mobileprism.autobot.model.datastore.UserDatastoreImpl
import ru.mobileprism.autobot.model.repository.AuthRepository
import ru.mobileprism.autobot.model.repository.AuthRepositoryImpl
import ru.mobileprism.autobot.model.repository.CityRepository
import ru.mobileprism.autobot.model.repository.CityRepositoryImpl
import ru.mobileprism.autobot.utils.CurrentUserHandler
import ru.mobileprism.autobot.viewmodels.*
import ru.mobileprism.autobot.viewmodels.scenarios.PriceChangedViewModel

val koinAppModule = module {

    single<AppSettings> { AppSettingsImpl(androidContext()) }
    single<UserDatastore> { UserDatastoreImpl(androidContext()) }


    single<AuthRepository> { AuthRepositoryImpl(apolloClient = get(), userDatastore = get()) }
    single<CityRepository> { CityRepositoryImpl(apolloClient = get(), userDatastore = get()) }

    single<AuthManager> { AuthManagerImpl(userDatastore = get()) }

    single { CurrentUserHandler(userDatastore = get()) }

    viewModel {
        MainActivityViewModel(userDatastore = get())
    }

    viewModel {
        ProfileViewModel(authManager = get(), userDatastore = get())
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

    viewModel {
        RegisterViewModel(cityRepository = get(), authManager = get())
    }

    viewModel { PriceChangedViewModel() }


}


