package ru.mobileprism.autoredemption.utils

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationManagerCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import ru.mobileprism.autoredemption.R
import ru.mobileprism.autoredemption.compose.custom.DefaultDialog


@Composable
fun checkNotificationPolicyAccess(
    notificationManager: NotificationManager,
    context: Context
): Boolean {
    if (notificationManager.areNotificationsEnabled()) {
        return true
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) PermissionDialog(context)
        else OldPermissionDialog(context = context)
    }
    return false
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun PermissionDialog(context: Context) {
    val openDialog = remember { mutableStateOf(true) }
    val smsPermissions =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    LaunchedEffect(smsPermissions.status) {
        if (smsPermissions.status.isGranted) {
            openDialog.value = false
        }
    }

    if (openDialog.value) {
        DefaultDialog(onDismiss = {
            openDialog.value = false
        },
            primaryText = stringResource(R.string.notification_permissions_required),
            positiveButtonText = stringResource(R.string.allow),
            negativeButtonText = stringResource(R.string.cancel),
            onPositiveClick = {
                if (smsPermissions.status.shouldShowRationale) {
                    context.startSmsSettings()
                } else {
                    smsPermissions.launchPermissionRequest()
                }
            },
            onNegativeClick = {
                openDialog.value = false
            }
        )
    }
}


@Composable
internal fun OldPermissionDialog(context: Context) {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        DefaultDialog(onDismiss = {
            openDialog.value = false
        },
            primaryText = stringResource(R.string.notification_permissions_required),
            positiveButtonText = stringResource(R.string.allow),
            negativeButtonText = stringResource(R.string.cancel),
            onPositiveClick = { context.startSmsSettings() },
            onNegativeClick = { openDialog.value = false })
    }
}

private fun Context.startSmsSettings() {
    try {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    } catch (e: Exception) {
        // TODO:
        Log.w("TAG", e.message ?: "")
    }
}
