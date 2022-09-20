package ru.mobileprism.autoredemption.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.datastore.AppSettingsImpl

val koinAppModule = module {

    single<AppSettings> { AppSettingsImpl(androidContext()) }

}