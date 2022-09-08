package ru.mobileprism.autoredemption

import android.app.ActivityManager
import android.app.Service
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.ComponentActivity


@Suppress("DEPRECATION") // Deprecated for third party Services.
fun <T> Context.isServiceRunning(service: Class<T>) =
    (getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }

fun Context.getSmsManager(): SmsManager {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        applicationContext.getSystemService<SmsManager>(SmsManager::class.java)
    } else SmsManager.getDefault()
}

fun Context.showToast(s: String) {
    Toast.makeText(
        this,
        s,
        Toast.LENGTH_SHORT
    ).show()
}

fun Context.startSmsService() {
    val serviceIntent = Intent(this, ForegroundService::class.java)
    serviceIntent.putExtra("inputExtra", "Сервис для автоматической отправки сообщений")
    //serviceIntent.putExtra("numbers", numbers.toTypedArray())
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(serviceIntent)
    } else {
        startService(serviceIntent)
    }
}

fun Context.stopService() {
    val serviceIntent = Intent(this, ForegroundService::class.java)
    stopService(serviceIntent)
}

fun Context.disableBatteryOptimizations() {
    try {
        val packageName = packageName
        val pm = getSystemService(ComponentActivity.POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent().apply {
                action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        }
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }
}