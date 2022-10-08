package ru.mobileprism.autoredemption.di

import com.apollographql.apollo3.ApolloClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.model.datastore.AppSettingsImpl
import ru.mobileprism.autoredemption.viewmodels.HomeViewModel

val koinAppModule = module {

    single<AppSettings> { AppSettingsImpl(androidContext()) }

    viewModel {
        HomeViewModel(get())
    }



}


