package ru.mobileprism.autoredemption

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class BaseApp: Application() {

    override fun onCreate() {
        super.onCreate()


        // Create the NotificationChannel
        val name = applicationContext.packageName
        val descriptionText = applicationContext.packageName
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(applicationContext.packageName, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}