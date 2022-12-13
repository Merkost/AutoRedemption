package ru.mobileprism.autobot.utils

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import ru.mobileprism.autobot.model.datastore.UserDatastore
import ru.mobileprism.autobot.model.datastore.UserEntity

class CurrentUserHandler(userDatastore: UserDatastore): LifecycleEventObserver {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + job)

    val currentUser = userDatastore.getCurrentUser
        .stateIn(coroutineScope, SharingStarted.Eagerly, UserEntity())

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event) {
            /*Lifecycle.Event.ON_CREATE -> TODO()
            Lifecycle.Event.ON_START -> TODO()
            Lifecycle.Event.ON_RESUME -> TODO()
            Lifecycle.Event.ON_PAUSE -> TODO()*/
            Lifecycle.Event.ON_STOP -> {
                coroutineScope.cancel()
            }
            /*Lifecycle.Event.ON_DESTROY -> TODO()
            Lifecycle.Event.ON_ANY -> TODO()*/
            else -> {}
        }
    }

}