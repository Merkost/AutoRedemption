package ru.mobileprism.autoredemption.workmanager

import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import androidx.work.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.mobileprism.autoredemption.Constants
import ru.mobileprism.autoredemption.R
import ru.mobileprism.autoredemption.datastore.AppSettings
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class SendSMSWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {

    private val settings: AppSettings by inject()

    private val smsManager = appContext.getSystemService(SmsManager::class.java)

    companion object {
        fun getSendSMSWork(numbers: List<String>): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<SendSMSWorker>(15, TimeUnit.MINUTES)
                .setInputData(
                    Data.Builder()
                        .putStringArray(NUMBERS_ARG, numbers.toTypedArray())
                        .build()
                )
                .setConstraints(
                    Constraints.Builder()
                        //.setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        setBackoffCriteria(
                            BackoffPolicy.EXPONENTIAL,
                            java.time.Duration.ofSeconds(5)
                        )
                    }
                }.build()

        // Custom equivalent format with a fixed Locale
        var dtfCustom: DateTimeFormatter =
            DateTimeFormatter.ofPattern("d MMM, HH:mm:ss", Locale("ru-RU"))

        const val NAME: String = "SendSMSWorker"
        private const val NUMBERS_ARG: String = "numbers"
    }

    override suspend fun doWork(): Result {
        //val numbers = inputData.getStringArray(NUMBERS_ARG)
        setForegroundAsync(showNotification())
        val smsSettings = settings.appSettings.first()
        val now = LocalDateTime.now()
        val lastTime = settings.lastTimeSmsSent.first()
        val numbers: List<String> = if (smsSettings.debugMode) Constants.DEBUG_NUMBERS
        else smsSettings.numbers

        val dur = java.time.Duration.between(lastTime, now).abs().toMinutes()

        if (dur > 14) {

            settings.saveLastTimeSmsSent(LocalDateTime.now())

            numbers.forEach { number ->
                val text = ""
                smsManager.sendTextMessage(
                    number,
                    null,
                    if (smsSettings.timeInText) LocalDateTime.now().format(dtfCustom)
                            + " " + text else text/* + " " + smsSettings.smsMessage*/,
                    null,
                    null
                )
                delay(smsSettings.messagesDelay)
            }
            return Result.success()
        } else {
            return Result.failure()
        }

        // Indicate whether the work finished successfully with the Result
        //return Result.success()
    }

    private fun showNotification(): ForegroundInfo {
        return ForegroundInfo(
            Random().nextInt(),
            NotificationCompat.Builder(
                applicationContext,
                applicationContext.packageName
            )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Отправляем сообщения")
                .setProgress(100, 0, true)
                .setOngoing(true)
                .build()
        )
    }


}