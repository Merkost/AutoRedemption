package ru.mobileprism.autoredemption.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.mobileprism.autoredemption.model.datastore.UserDatastore

class TokensInterceptor(private val userDatastore: UserDatastore) : Interceptor, LifecycleEventObserver {

//    private val coroutineScope = MainScope()

//    val token = tokenDatastore.getUserToken
//        .stateIn(coroutineScope, SharingStarted.Eagerly, null)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { userDatastore.getUserToken.firstOrNull() }
        token?.let {
            val request: Request = chain.request()

            val newRequest: Request = request.newBuilder()
                .addHeader("AutoBotToken", token)
                .build()
            return chain.proceed(newRequest)
        }
        return chain.proceed(chain.request())
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            /*Lifecycle.Event.ON_CREATE -> TODO()
            Lifecycle.Event.ON_START -> TODO()
            Lifecycle.Event.ON_RESUME -> TODO()
            Lifecycle.Event.ON_PAUSE -> TODO()
            Lifecycle.Event.ON_STOP -> TODO()*/
            Lifecycle.Event.ON_DESTROY -> {

            }
            /*Lifecycle.Event.ON_ANY -> TODO()*/
            else -> {}
        }
    }

}