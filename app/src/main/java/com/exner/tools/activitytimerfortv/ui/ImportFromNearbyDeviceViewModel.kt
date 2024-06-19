package com.exner.tools.activitytimerfortv.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import com.exner.tools.activitytimerfortv.network.TimerConnectionLifecycleCallback
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Strategy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class ProcessStateConstants {
    AWAITING_PERMISSIONS, // IDLE
    PERMISSIONS_GRANTED,
    PERMISSIONS_DENIED,
    ADVERTISING,
    DISCOVERED,
    AUTHENTICATED_OK,
    AUTHENTICATED_DENIED,
    CONNECTION_ESTABLISHED,
    CONNECTION_FAILED,
    PROCESSES_SELECTED,
    RECEIVING,
    DISCONNECTED,
    DONE,
    CANCELLED,
    ERROR
}

const val endpointId = "com.exner.tools.activitytimerfortv"
const val userName = "Anonymous"

data class ProcessState(
    val currentState: ProcessStateConstants = ProcessStateConstants.AWAITING_PERMISSIONS,
    val message: String = ""
)

// convencience function for all those invalid transitions
fun invalidTransitionProcessState(
    currentState: ProcessStateConstants,
    newState: ProcessStateConstants
): ProcessState {
    return ProcessState(
        ProcessStateConstants.ERROR,
        "invalid transition: ${currentState.name} > ${newState.name}"
    )
}

@HiltViewModel
class ImportFromNearbyDeviceViewModel @Inject constructor(
    repository: TimerDataRepository,
) : ViewModel() {

    private val _processStateFlow = MutableStateFlow(ProcessState())
    val processStateFlow: StateFlow<ProcessState> = _processStateFlow.asStateFlow()

    lateinit var connectionLifecycleCallback: TimerConnectionLifecycleCallback
    lateinit var connectionsClient: ConnectionsClient

    fun provideLifecycleCallback(connectionLifecycleCallback: TimerConnectionLifecycleCallback) {
        this.connectionLifecycleCallback = connectionLifecycleCallback
    }
    fun provideConnectionsClient(connectionsClient: ConnectionsClient) {
        this.connectionsClient = connectionsClient
    }

    fun transitionToNewState(
        newState: ProcessStateConstants,
        message: String = "OK"
    ) {
        // there is a lot of logic to do here!
        when (processStateFlow.value.currentState) {
            ProcessStateConstants.AWAITING_PERMISSIONS -> {
                // nothing to do, this is handled in the UI entirely
                when (newState) {
                    ProcessStateConstants.PERMISSIONS_GRANTED -> {
                        _processStateFlow.value = ProcessState(newState, "OK")
                    }

                    ProcessStateConstants.PERMISSIONS_DENIED -> {
                        _processStateFlow.value = ProcessState(newState, "Denied: $message")
                    }

                    ProcessStateConstants.CANCELLED -> {
                        _processStateFlow.value = ProcessState(newState, "Cancelled")
                    }

                    else -> {
                        _processStateFlow.value = invalidTransitionProcessState(
                            currentState = processStateFlow.value.currentState,
                            newState = newState
                        )
                    }
                }
            }

            ProcessStateConstants.PERMISSIONS_GRANTED -> {
                // nothing to do, this is handled in the UI entirely
                when (newState) {
                    ProcessStateConstants.ADVERTISING -> {
                        // trigger the actual advertising
                        val advertisingOptions: AdvertisingOptions =
                            AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT)
                                .build()
                        Log.d("STARTADV", "Starting to advertise...")
                        connectionsClient
                            .startAdvertising(
                                userName,
                                endpointId,
                                connectionLifecycleCallback,
                                advertisingOptions
                            )
                            .addOnSuccessListener { unused: Void? ->
                                Log.d("STARTADV", "Advertising started")
                                _processStateFlow.value = ProcessState(ProcessStateConstants.ADVERTISING, "OK")
                            }
                            .addOnFailureListener { e: Exception? ->
                                val errorMessage = "Error starting advertising" + if (e != null) {
                                    ": ${e.message}"
                                } else {
                                    ""
                                }
                            }
                        _processStateFlow.value = ProcessState(newState, "OK")
                    }

                    ProcessStateConstants.CANCELLED -> {
                        connectionsClient.stopAdvertising()
                        _processStateFlow.value = ProcessState(newState, "Cancelled")
                    }

                    else -> {
                        _processStateFlow.value = invalidTransitionProcessState(
                            currentState = processStateFlow.value.currentState,
                            newState = newState
                        )
                    }
                }
            }

            ProcessStateConstants.PERMISSIONS_DENIED -> {
                // nothing to do, this is handled in the UI entirely
                when (newState) {
                    ProcessStateConstants.AWAITING_PERMISSIONS -> {
                        _processStateFlow.value = ProcessState(newState, "OK")
                    }

                    ProcessStateConstants.CANCELLED -> {
                        _processStateFlow.value = ProcessState(newState, "Cancelled")
                    }

                    else -> {
                        _processStateFlow.value = invalidTransitionProcessState(
                            currentState = processStateFlow.value.currentState,
                            newState = newState
                        )
                    }
                }
            }

            ProcessStateConstants.ADVERTISING -> {
                // stop advertising
                connectionsClient.stopAdvertising()
                // nothing to do, this is handled in the UI
                when (newState) {
                    ProcessStateConstants.DISCOVERED -> {
                        _processStateFlow.value = ProcessState(newState, "OK")
                    }

                    ProcessStateConstants.CANCELLED -> {
                        _processStateFlow.value = ProcessState(newState, "Cancelled")
                    }

                    else -> {
                        _processStateFlow.value = invalidTransitionProcessState(
                            currentState = processStateFlow.value.currentState,
                            newState = newState
                        )
                    }
                }
            }
            ProcessStateConstants.DISCOVERED -> {
                // trigger authentication (it'll happen on both devices)
            }
            ProcessStateConstants.AUTHENTICATED_OK -> TODO()
            ProcessStateConstants.AUTHENTICATED_DENIED -> TODO()
            ProcessStateConstants.CONNECTION_ESTABLISHED -> TODO()
            ProcessStateConstants.CONNECTION_FAILED -> TODO()
            ProcessStateConstants.PROCESSES_SELECTED -> TODO()
            ProcessStateConstants.RECEIVING -> TODO()
            ProcessStateConstants.DISCONNECTED -> TODO()
            ProcessStateConstants.DONE -> TODO()
            ProcessStateConstants.ERROR -> TODO()
            ProcessStateConstants.CANCELLED -> {
                // just in case...
                connectionsClient.stopAllEndpoints()
                connectionsClient.stopDiscovery()
            }
        }
    }

    fun stopAdvertising(context: Context) {
        Log.d("STOPADV", "Stop advertising.")
        Nearby.getConnectionsClient(context).stopAdvertising()
    }
}