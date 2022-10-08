package ru.mobileprism.autoredemption.workmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ru.mobileprism.autoredemption.utils.startSmsService

class DeviceBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.v("log_tag", "Action :: "+intent.action);
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            context.startSmsService()
            /*val serviceIntent = Intent(context, ForegroundService::class.java)
            context.startService(serviceIntent)*/
        }
    }
}