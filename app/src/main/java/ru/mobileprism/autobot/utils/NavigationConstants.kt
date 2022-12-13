package ru.mobileprism.autobot.utils

import android.os.Parcelable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions

object Arguments {
    const val PHONE_AUTH = "phone_auth_arg"
}

fun NavController.navigate(route: String, vararg args: Pair<String, Parcelable>, navOptions: NavOptions? = null) {
    navigate(route, navOptions)

    requireNotNull(currentBackStackEntry?.arguments).apply {
        args.forEach { (key: String, arg: Parcelable) ->
            putParcelable(key, arg)
        }
    }
}

inline fun <reified T : Parcelable> NavBackStackEntry.requiredArg(key: String): T {
    return requireNotNull(arguments) { "arguments bundle is null" }.run {
        requireNotNull(getParcelable(key)) { "argument for $key is null" }
    }
}

fun NavController.navigateSingleTop(route: String) {
    navigate(route, navOptions { launchSingleTop = true })
}

fun NavController.navigateSingleTop(route: String, vararg args: Pair<String, Parcelable>) {
    navigate(route, args = args, navOptions { launchSingleTop = true })
}