package ru.mobileprism.autoredemption.workmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.mobileprism.autoredemption.Constants
import ru.mobileprism.autoredemption.startSmsService

class DeviceBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            context.startSmsService(Constants.DEBUG_NUMBERS)
        }
    }
}