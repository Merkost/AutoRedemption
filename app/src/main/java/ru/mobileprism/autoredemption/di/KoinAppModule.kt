package ru.mobileprism.autoredemption.di

import ru.mobileprism.autoredemption.viewmodels.SmsVerificationViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.datastore.AppSettingsImpl
import ru.mobileprism.autoredemption.model.repository.AuthRepository
import ru.mobileprism.autoredemption.model.repository.AuthRepositoryImpl
import ru.mobileprism.autoredemption.viewmodels.HomeViewModel
import ru.mobileprism.autoredemption.viewmodels.PhoneEnteringViewModel

val koinAppModule = module {

    single<AppSettings> { AppSettingsImpl(androidContext()) }

    factory<AuthRepository> { AuthRepositoryImpl(get()) }

    viewModel {
        HomeViewModel(appSettings = get())
    }

    viewModel { PhoneEnteringViewModel(authRepository = get()) }
    viewModel { SmsVerificationViewModel(it.get(), authRepository = get()) }


}


