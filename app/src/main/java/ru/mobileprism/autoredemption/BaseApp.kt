package ru.mobileprism.autoredemption

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import ru.mobileprism.autoredemption.di.koinAppModule

@HiltAndroidApp
class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@BaseApp)
            modules(
                listOf(
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

    private fun createServiceNotificationChannel() {
        val name = ForegroundService.CHANNEL_ID
        val descriptionText = applicationContext.packageName
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(ForegroundService.CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}