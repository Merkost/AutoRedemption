package ru.mobileprism.autoredemption

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import ru.mobileprism.autoredemption.di.koinAppModule
import ru.mobileprism.autoredemption.di.networkModule
import ru.mobileprism.autoredemption.workmanager.ForegroundService

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

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