package ru.mobileprism.autoredemption.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.mobileprism.autoredemption.datastore.AppSettings
import ru.mobileprism.autoredemption.datastore.AppSettingsImpl

val koinAppModule = module {

    single<AppSettings> { AppSettingsImpl(androidContext()) }

}