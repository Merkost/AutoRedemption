package ru.mobileprism.autobot

import android.R
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import cat.ereza.customactivityoncrash.config.CaocConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import ru.mobileprism.autobot.di.koinAppModule
import ru.mobileprism.autobot.di.networkModule
import ru.mobileprism.autobot.utils.Constants
import ru.mobileprism.autobot.workmanager.ForegroundService


class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
            .enabled(Constants.isDebug.not()) //default: true
            .showErrorDetails(true) //default: true
            .showRestartButton(false) //default: true
            .logErrorOnRestart(false) //default: true
            .trackActivities(true) //default: false
            .minTimeBetweenCrashesMs(2000) //default: 3000
            /*.errorDrawable(R.drawable.ic_custom_drawable) //default: bug image
            .restartActivity(YourCustomActivity::class.java) //default: null (your app's launch activity)
            .errorActivity(YourCustomErrorActivity::class.java) //default: null (default error activity)
            .eventListener(YourCustomEventListener()) //default: null
            .customCrashDataCollector(YourCustomCrashDataCollector()) //default: null
            */.apply()

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@BaseApp)
            modules(
                listOf(
                    networkModule,
                    koinAppModule,
                )
            )
        }

        // Create the NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createMainNotificationChannel()
            createServiceNotificationChannel()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMainNotificationChannel() {
        val name = applicationContext.packageName
        val descriptionText = applicationContext.packageName
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(applicationContext.packageName, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createServiceNotificationChannel() {
        val name = ForegroundService.CHANNEL_ID
        val descriptionText = applicationContext.packageName
        val importance = NotificationManager.IMPORTANCE_NONE
        val mChannel = NotificationChannel(ForegroundService.CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}