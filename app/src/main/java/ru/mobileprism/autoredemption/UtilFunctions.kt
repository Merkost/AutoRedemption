package ru.mobileprism.autoredemption

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity

fun Context.startSmsService(numbers: List<String>) {
    if (numbers.isEmpty()) {
        Toast.makeText(this, "Нет номеров для отправки сообщений!", Toast.LENGTH_SHORT).show()
    } else {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        serviceIntent.putExtra("inputExtra", "Сервис для автоматической отправки сообщений")
        serviceIntent.putExtra("numbers", numbers.toTypedArray())
        applicationContext.startForegroundService(serviceIntent)

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