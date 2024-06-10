package com.exner.tools.activitytimerfortv.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import com.exner.tools.activitytimerfortv.network.TimerConnectionLifecycleCallback
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.Strategy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class ProcessStateConstants {
    AWAITING_PERMISSIONS,
    PERMISSIONS_DENIED,
    AWAITING_DISCOVERY,
    DISCOVERED,
    CONNECTED,
    RECEIVING,
    DISCONNECTED,
    ERROR
}
data class ProcessState(
    val currentState: ProcessStateConstants = ProcessStateConstants.AWAITING_PERMISSIONS
)

@HiltViewModel
class ImportFromNearbyDeviceViewModel @Inject constructor(
    repository: TimerDataRepository
): ViewModel() {

    private val _processStateFlow = MutableStateFlow(ProcessState())
    val processStateFlow: StateFlow<ProcessState> = _processStateFlow.asStateFlow()

    fun setCurrentState(newState: ProcessStateConstants) {
        _processStateFlow.value = ProcessState(newState)
    }

fun startAdvertising(context: Context) {
        val advertisingOptions: AdvertisingOptions =
            AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        val connectionLifecycleCallback = TimerConnectionLifecycleCallback()
        Nearby.getConnectionsClient(context)
            .startAdvertising(
                "Anonymous", "com.exner.tools.activitytimerfortv", connectionLifecycleCallback, advertisingOptions
            )
            .addOnSuccessListener { unused: Void? -> }
            .addOnFailureListener { e: Exception? -> }
    }
}