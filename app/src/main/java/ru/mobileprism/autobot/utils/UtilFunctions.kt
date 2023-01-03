package ru.mobileprism.autobot.utils

import android.app.ActivityManager
import android.app.Service
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.singleOrNull
import ru.mobileprism.autobot.workmanager.ForegroundService
import ru.mobileprism.autobot.model.repository.AutoBotError
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.regex.Matcher
import java.util.regex.Pattern


fun Context.launchAppSettings() {
    try {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    } catch (e: Exception) {
        Log.w("TAG", e.message ?: "")
    }
}

@Suppress("DEPRECATION") // Deprecated for third party Services.
fun <T> Context.isServiceRunning(service: Class<T>) =
    (getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }

fun Context.getDefaultSmsManager(): SmsManager {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService<SmsManager>(SmsManager::class.java)
    } else {
        SmsManager.getDefault()
    }
}

fun Context.getSmsManager(subscriptionId: Int): SmsManager {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService<SmsManager>(SmsManager::class.java).createForSubscriptionId(subscriptionId)
    } else {
        SmsManager.getSmsManagerForSubscriptionId(subscriptionId)
    }
}

suspend fun Context.tryGetExactSmsManager(selectedSimId: Flow<Int?>): SmsManager {
    val subscriptionId = selectedSimId.firstOrNull()
    return subscriptionId?.let {
        getSmsManager(it)
    } ?: getDefaultSmsManager()

}

fun Context.getSubscriptionManager(): SubscriptionManager {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService<SubscriptionManager>(SubscriptionManager::class.java)
    } else {
        SubscriptionManager.from(this)
    }
}

fun Context.startSmsService() {
    val serviceIntent = Intent(this, ForegroundService::class.java)
    serviceIntent.putExtra("inputExtra", "Сервис для автоматической отправки сообщений")
    //serviceIntent.putExtra("numbers", numbers.toTypedArray())
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ContextCompat.startForegroundService(this, serviceIntent)
        Log.d("AUTOREDEMPTION", "startForegroundService received")
    } else {
        startService(serviceIntent)
        Log.d("AUTOREDEMPTION", "startService received")
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

fun Context.autoStart() {
    try {
        val intent = Intent()
        val manufacturer = Build.MANUFACTURER
        if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
        } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
            )
        } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
            )
        } else if ("Letv".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.letv.android.letvsafe",
                "com.letv.android.letvsafe.AutobootManageActivity"
            )
        } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.optimize.process.ProtectActivity"
            )
        } else if ("oneplus".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.oneplus.security",
                "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
            )
        }
        val list: List<ResolveInfo> =
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list.isNotEmpty()) {
            startActivity(intent)
        }
    } catch (e: Exception) {
        Log.e("exc", e.toString())
    }
}

fun Context.showToast(text: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(applicationContext, text, length).show()
}

fun Context.showError(autoBotError: AutoBotError) {
    when(Constants.isDebug) {
        true -> {
            autoBotError.message?.let {
                showToast(it)
            } ?: showToast(getString(autoBotError.messageResource))
        }
        else -> {
            showToast(getString(autoBotError.messageResource))
        }
    }
}

val String.toLocalDateTimeOrNull: LocalDateTime?
    get() = kotlin.runCatching { LocalDateTime.parse(this) }.getOrNull()

val String.toZonedDateTimeOrNull: ZonedDateTime?
    get() = kotlin.runCatching { ZonedDateTime.parse(this) }.getOrNull()

fun extractDigits(str: String): String {
    val p: Pattern = Pattern.compile("(\\d{6})")
    val m: Matcher = p.matcher(str)
    return if (m.find()) {
        m.group(0) ?: ""
    } else ""
}

fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    clickable(
        enabled = enabled,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}