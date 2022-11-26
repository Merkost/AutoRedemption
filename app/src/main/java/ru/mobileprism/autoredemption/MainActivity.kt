package ru.mobileprism.autoredemption

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ru.mobileprism.autoredemption.compose.AutoBotApp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.mobileprism.autoredemption.compose.MainDestinations
import ru.mobileprism.autoredemption.compose.screens.*
import ru.mobileprism.autoredemption.model.entities.AuthState
import ru.mobileprism.autoredemption.compose.screens.auth.LoginDestinations
import ru.mobileprism.autoredemption.compose.screens.home.MainScreen
import ru.mobileprism.autoredemption.utils.checkNotificationPolicyAccess
import ru.mobileprism.autoredemption.utils.showToast
import ru.mobileprism.autoredemption.model.datastore.AppSettings
import ru.mobileprism.autoredemption.ui.theme3.AutoBotTheme
import ru.mobileprism.autoredemption.utils.*


class MainActivity : ComponentActivity() {


    companion object {
        const val splashFadeDurationMillis = 300
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainActivityViewModel = getViewModel()
        var authState: AuthState? = null

        lifecycleScope.launchWhenCreated {
            viewModel.authState.collect { authState = it }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                authState == null
            }
            setOnExitAnimationListener { splashScreenViewProvider ->
                // Get icon instance and start a fade out animation

                splashScreenViewProvider.view
                    .animate()
                    .setDuration(splashFadeDurationMillis.toLong())
                    .alpha(0f)
                    .start()


                /*splashScreenViewProvider.iconView
                    .animate()
                    .setDuration(splashFadeDurationMillis.toLong())
                    .alpha(0f)
                    .start()*/
            }
        }


        setTheme(R.style.Theme_AnimatedSplashScreen)
        setContent {
            AutoBotTheme {
                Distribution(viewModel.authState.collectAsState().value)
            }
        }


    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun Distribution(authState: AuthState?) {
        when (authState) {
            is AuthState.Logged -> {

                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val appSettings: AppSettings = get()
                val chosenSimCardFromSettings = appSettings.selectedSimId.collectAsState(1)

                val requiredPermissions = rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.SEND_SMS
                    )
                )

                if (requiredPermissions.allPermissionsGranted.not() || chosenSimCardFromSettings.value == null
                ) {
                    AutoBotTheme {
                        PermissionsScreen()
                        /*Dialog(
                            onDismissRequest = { *//*TODO*//* },
                            properties = DialogProperties(
                                dismissOnBackPress = false,
                                dismissOnClickOutside = false,
                                securePolicy = SecureFlagPolicy.SecureOff,
                            )
                        ) {
                            PermissionsScreen()
                        }*/
                    }
                } else {
                        checkNotificationPolicyAccess(notificationManager, this)
                        AutoBotApp()
                }
//                AutoBotApp()
            }
            AuthState.NotLogged, null -> {
                AutoBotApp(startRoute = MainDestinations.AUTH_ROUTE)
            }
        }
    }
}

/*@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen() {
    val context = LocalContext.current
    val notificationManager: NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    val simsPermissions = rememberPermissionState(permission = Manifest.permission.READ_PHONE_STATE)


    val requiredPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_PHONE_STATE,
        )
    )


    Scaffold(modifier = Modifier.systemBarsPadding()) {
        DefaultColumn(modifier = Modifier.padding(30.dp)) {
            ActionCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(50.dp), onClick = {
                    if (simsPermissions.status.shouldShowRationale) {
                        context.showToast("Необходимо выдать все разрешения в настройках")
                        try {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", context.packageName, null)
                            intent.data = uri
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // TODO:
                            Log.w("TAG", e.message ?: "")
                        }
                    } else {
                        simsPermissions.launchPermissionRequest()
                    }
                }, text = "Разрешение на просмотр активных сим карт на устройстве",
                isPermissionGranted = simsPermissions.status.isGranted
            )
        }
    }
}*/

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen() {
    val context = LocalContext.current
    val notificationManager: NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

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
        MediumTopAppBar(
            title = {
                Text(text = "Разрешения для работы")
            }
        )
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
                val permission = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

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

    val chosenSimCard =
        remember(chosenSimCardFromSettings.value) {
            mutableStateOf(
                simCards
                    .find { it.subscriptionId == chosenSimCardFromSettings.value }
                    ?.subscriptionId
            )
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
            text = "Выберите сим карту для отправки СМС:",
            modifier = Modifier.weight(1f, false)
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
    modifier: Modifier,
    text: String,
    onClick: () -> Unit,
    isPermissionGranted: Boolean
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

@Composable
fun DefaultColumn(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(12.dp),
    function: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {
        function()
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AutoBotTheme {
        MainScreen({}) {}
    }
}

/**
 * A composable function that returns the [Resources]. It will be recomposed when `Configuration`
 * gets updated.
 */
@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}
