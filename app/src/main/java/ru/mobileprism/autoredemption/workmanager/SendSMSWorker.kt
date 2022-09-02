package ru.mobileprism.autoredemption.workmanager

import android.content.Context
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import ru.mobileprism.autoredemption.R
import java.util.*

class SendSMSWorker(private val appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    private val smsManager = SmsManager.getDefault() as SmsManager

    companion object {
        const val NAME: String = "SendSMSWorker"
        const val NUMBERS_ARG: String = "numbers"
    }

    override suspend fun doWork(): Result {
        setForeground(showNotification())
        delay(5000L)

        val numbers = inputData.getStringArray(NUMBERS_ARG)
        numbers?.let {
            it.forEach { number ->
                smsManager.sendTextMessage(number, null, "sms message", null, null)
            }
        }

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    private fun showNotification(): ForegroundInfo {
        return ForegroundInfo(
            Random().nextInt(),
            NotificationCompat.Builder(
                applicationContext,
                applicationContext.packageName
                /*NotificationChannel.DEFAULT_CHANNEL_ID*/
            )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Отправляем ссобщения")
                .setProgress(100, 0, true)
                .setOngoing(true)
                .build()
        )
    }
}