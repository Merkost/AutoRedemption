package ru.mobileprism.autobot.compose.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.mobileprism.autobot.compose.custom.DefaultColumn
import ru.mobileprism.autobot.model.datastore.AppSettings
import ru.mobileprism.autobot.utils.checkNotificationPolicyAccess
import ru.mobileprism.autobot.utils.getSubscriptionManager
import ru.mobileprism.autobot.utils.launchAppSettings
import ru.mobileprism.autobot.utils.showToast

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen() {
    val context = LocalContext.current
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    var areNotificationsEnabled by remember { mutableStateOf(notificationManager.areNotificationsEnabled()) }

    LaunchedEffect(Unit) {
        if (notificationManager.areNotificationsEnabled()) {
            areNotificationsEnabled = true
        }
    }

    val readSimPermission =
        rememberPermissionState(permission = Manifest.permission.READ_PHONE_STATE)
    val sendSmsPermission = rememberPermissionState(permission = Manifest.permission.SEND_SMS)

    Scaffold(topBar = {
        MediumTopAppBar(title = {
            Text(text = "Разрешения для работы")
        })
    }, modifier = Modifier.systemBarsPadding()) {
        DefaultColumn(
            modifier = Modifier
                .padding(25.dp)
                .padding(it)
        ) {

            AnimatedVisibility(visible = readSimPermission.status.isGranted) {
                ChooseSimScreen()
            }

            ActionCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(50.dp),
                onClick = {
                    if (readSimPermission.status.shouldShowRationale) {
                        context.showToast("Необходимо выдать разрешения в настройках")
                        context.launchAppSettings()
                    } else {
                        readSimPermission.launchPermissionRequest()
                    }
                },
                text = "Разрешение на просмотр активных сим карт на устройстве",
                isPermissionGranted = readSimPermission.status.isGranted
            )

            ActionCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(50.dp),
                onClick = {
                    if (sendSmsPermission.status.shouldShowRationale) {
                        context.showToast("Необходимо выдать разрешения в настройках")
                        context.launchAppSettings()
                    } else {
                        sendSmsPermission.launchPermissionRequest()
                    }
                },
                text = "Разрешение на отправку СМС сообщений",
                isPermissionGranted = sendSmsPermission.status.isGranted
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permission =
                    rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

                ActionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(50.dp),
                    onClick = {
                        if (permission.status.shouldShowRationale) {
                            context.showToast("Необходимо выдать разрешения в настройках")
                            context.launchAppSettings()
                        } else {
                            permission.launchPermissionRequest()
                        }
                    },
                    text = "Разрешение на показ уведомлений",
                    isPermissionGranted = permission.status.isGranted
                )
            } else {
                checkNotificationPolicyAccess(notificationManager, context)
                ActionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(50.dp),
                    onClick = {},
                    text = "Разрешение на показ уведомлений",
                    isPermissionGranted = true
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun ChooseSimScreen() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val subscriptionManager = context.getSubscriptionManager()

    val defaultSubscriptionId = SmsManager.getDefaultSmsSubscriptionId()
//                val smsManager = SmsManager.getSmsManagerForSubscriptionId(defaultSubscriptionId) ?: SmsManager.getDefault()
    var defaultSubscriptionInfo: SubscriptionInfo? =
        subscriptionManager.getActiveSubscriptionInfo(defaultSubscriptionId)

    val defaultSimCard: SubscriptionInfo? =
        subscriptionManager.getActiveSubscriptionInfo(defaultSubscriptionId)

    val simCards: List<SubscriptionInfo> = remember {
        subscriptionManager.activeSubscriptionInfoList
    }
    val appSettings: AppSettings = get()
    val chosenSimCardFromSettings = appSettings.selectedSimId.collectAsState(null)

    val chosenSimCard = remember(chosenSimCardFromSettings.value) {
        mutableStateOf(simCards.find { it.subscriptionId == chosenSimCardFromSettings.value }?.subscriptionId)
    }

    /*LaunchedEffect(chosenSimCard.value) {
        chosenSimCard.value?.let {
            appSettings.saveSelectedSimId(it.subscriptionId)
        }
    }*/

    /*if (localSubscriptionManager.activeSubscriptionInfoCount > 1) {
        val localList: List<*> = localSubscriptionManager.activeSubscriptionInfoList
        val simInfo = localList[simID] as SubscriptionInfo
        SmsManager.getSmsManagerForSubscriptionId(simInfo.subscriptionId)
            .sendTextMessage(toNum, null, smsText, null, null)
    }

    val subscriptionManager = SubscriptionManager.from(this)
    Log.d("abc", smsManager.subscriptionId.toString())
    smsManager.createForSubscriptionId()*/


    Crossfade(targetState = chosenSimCard.value != null) {
        when (it) {
            true -> {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    ChooseSimView(simCards, chosenSimCard.value) {
                        coroutineScope.launch { appSettings.saveSelectedSimId(it.subscriptionId) }
                    }
                }
            }
            else -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    ChooseSimView(simCards, chosenSimCard.value) {
                        coroutineScope.launch { appSettings.saveSelectedSimId(it.subscriptionId) }
                    }
                }
            }
        }
    }
}

@Composable
fun ChooseSimView(
    simCards: List<SubscriptionInfo>,
    chosenSimCardSubsId: Int?,
    onChooseSim: (SubscriptionInfo) -> Unit,
) {
    Column(
        modifier = Modifier.padding(12.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Выберите сим карту для отправки СМС:", modifier = Modifier.weight(1f, false)
        )

        Column() {
            simCards.forEachIndexed { index, subscriptionInfo ->
                SimView(
                    index,
                    subscriptionInfo.carrierName.toString(),
                    subscriptionInfo.subscriptionId == chosenSimCardSubsId
                ) {
                    onChooseSim(subscriptionInfo)
                }
            }
        }
    }
}

@Composable
fun SimView(count: Int, name: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(Icons.Outlined.SimCard, "")
        Text(text = name)
        RadioButton(selected = isSelected, onClick = onClick)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(
    modifier: Modifier, text: String, onClick: () -> Unit, isPermissionGranted: Boolean
) {
    Crossfade(targetState = isPermissionGranted) {
        when (it) {
            true -> {
                ElevatedCard(modifier = modifier) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = text, modifier = Modifier.weight(1f, false))
                        Icon(
                            Icons.Outlined.Check,
                            "",
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                        )
                    }
                }
            }
            else -> {
                Card(modifier = modifier, onClick = onClick) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = text, modifier = Modifier.weight(1f, false))
                        Icon(
                            Icons.Outlined.PriorityHigh,
                            "",
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}