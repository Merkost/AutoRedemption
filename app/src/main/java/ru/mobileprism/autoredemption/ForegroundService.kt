package ru.mobileprism.autoredemption


import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.crazylegend.crashyreporter.CrashyReporter
import ru.mobileprism.autoredemption.workmanager.SendSMSWorker


class ForegroundService : Service() {

    private val workManager = WorkManager.getInstance(this)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when {
            intent?.action == ACTION_STOP_SERVICE -> {

                Log.d("ForegroundService", "Called to cancel service")

                workManager.cancelUniqueWork(SendSMSWorker.NAME)

                stopForeground(true)
                stopSelf()

                return START_NOT_STICKY
            }
            else -> {
                //val input = intent.getStringExtra("inputExtra")
                //val numbers = intent.getStringArrayExtra("numbers")
                setNotification()
                //do heavy work on a background thread

                workManager.enqueueUniquePeriodicWork(
                    SendSMSWorker.NAME, ExistingPeriodicWorkPolicy.KEEP,
                    SendSMSWorker.getSendSMSWork()
                )

                return START_STICKY
            }
        }


    }

    private fun setNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopSelf =
            Intent(this, ForegroundService::class.java).apply { action = ACTION_STOP_SERVICE }
        val pStopSelf =
            PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SMS-Сервис активен")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(R.drawable.ic_launcher_foreground, getString(R.string.stop), pStopSelf)
            //.setContentText(input)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            startForeground(1, notification)
        } else {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        restartService()
        super.onTaskRemoved(rootIntent)
    }

    private fun restartService() {
        runCatching {
            //create an intent that you want to start again.
            val intent = Intent(applicationContext, ForegroundService::class.java)
            val pendingIntent = PendingIntent.getService(
                this, 1, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager[AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000] =
                pendingIntent
        }.onFailure {
            CrashyReporter.logException(it)
        }
    }

    companion object {
        const val ACTION_STOP_SERVICE = "StopService"
        const val CHANNEL_ID = "ForegroundServiceChannel"
    }


}