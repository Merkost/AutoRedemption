package ru.mobileprism.autoredemption.workmanager

import android.content.Context
import android.telephony.SmsManager
import androidx.compose.runtime.collectAsState
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.mobileprism.autoredemption.R
import ru.mobileprism.autoredemption.datastore.AppSettings
import ru.mobileprism.autoredemption.datastore.AppSettingsEntity
import java.time.LocalDateTime
import java.util.*

class SendSMSWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {

    private val settings: AppSettings by inject()

    private val smsManager = appContext.getSystemService(SmsManager::class.java)

    companion object {
        const val NAME: String = "SendSMSWorker"
        const val NUMBERS_ARG: String = "numbers"
    }

    override suspend fun doWork(): Result {
        setForegroundAsync(showNotification())
        val smsSettings = settings.appSettings.first()

        val numbers = inputData.getStringArray(NUMBERS_ARG)
        numbers?.let {
            it.forEach { number ->
                val text = ""
                smsManager.sendTextMessage(
                    number,
                    null,
                    if (smsSettings.timeInText) LocalDateTime.now()
                        .toString() + " " + text else text/* + " " + smsSettings.smsMessage*/,
                    null,
                    null
                )
                delay(500)
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
            )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Отправляем ссобщения")
                .setProgress(100, 0, true)
                .setOngoing(true)
                .build()
        )
    }
}