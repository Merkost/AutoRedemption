package ru.mobileprism.autobot.utils

import android.app.Activity
import android.app.PendingIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity

@Composable
fun PhoneNumberConsent(
    onPhoneNumberFetchedFromDevice: (phoneNumber: String) -> Unit,
) {
    val TAG = "PhoneNumberConsent"
    val context = LocalContext.current

    val getPhoneNumberConsent =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult? ->
            if (result?.resultCode == Activity.RESULT_OK && result.data != null) {
                try {
                    val phoneNumber = Identity.getSignInClient(context).getPhoneNumberFromIntent(result.data)
                    Log.d(TAG,"Phone number fetched from auto fill")
                    onPhoneNumberFetchedFromDevice(phoneNumber)
                } catch(e: Exception) {
                    Log.e(TAG, "Phone Number Hint failed")
                }
            } else {
                Log.d(TAG,"No number selected or unavailable. User can type manually.")
            }
        }

    LaunchedEffect(Unit) {
        val request: GetPhoneNumberHintIntentRequest = GetPhoneNumberHintIntentRequest.builder().build()

        Identity.getSignInClient(context)
            .getPhoneNumberHintIntent(request)
            .addOnSuccessListener { result: PendingIntent ->
                try {
                    getPhoneNumberConsent.launch(IntentSenderRequest.Builder(result).build())
                } catch (e: Exception) {
                    Log.e(TAG, "Launching the PendingIntent failed")
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Phone Number Hint failed")
            }
    }
}