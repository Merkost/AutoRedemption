package ru.mobileprism.autoredemption


import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import ru.mobileprism.autoredemption.workmanager.SendSMSWorker


class ForegroundService : Service() {

    private val workManager = WorkManager.getInstance(this)

    override fun onCreate() {
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val input = intent.getStringExtra("inputExtra")
        val numbers = intent.getStringArrayExtra("numbers")
        setNotification()
        //do heavy work on a background thread

        workManager.enqueueUniquePeriodicWork(
            SendSMSWorker.NAME, ExistingPeriodicWorkPolicy.KEEP,
            getSendSMSWork(numbers?.toList() ?: listOf())
        )

        //stopSelf();
        return START_STICKY
    }

    private fun setNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SMS-Сервис активен")
            //.setContentText(input)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        //create an intent that you want to start again.
        val intent = Intent(applicationContext, ForegroundService::class.java)
        val pendingIntent = PendingIntent.getService(this, 1, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000] =
            pendingIntent
        super.onTaskRemoved(rootIntent)
    }

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
    }

}