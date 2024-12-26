package com.exner.tools.activitytimerfortv.ui

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class ConnectedProcessStateConstants {
    IDLE,
    AWAITING_PERMISSIONS,
    PERMISSIONS_GRANTED,
    PERMISSIONS_DENIED,
    START_ADVERTISING,
    ADVERTISING,
    DISCOVERED,
    CONNECTION_INITIATED,
    CONNECTION_ESTABLISHED,
    CONNECTION_FAILED,
    AUTHENTICATION_REQUESTED,
    AUTHENTICATION_OK,
    AUTHENTICATION_DENIED,
    RECEIVING,
    DONE,
    CANCELLED,
    ERROR
}

//const val endpointId = "com.exner.tools.ActivityTimer"
//const val userName = "Activity Timer for TV"

data class ConnectedProcessState(
    val currentState: ConnectedProcessStateConstants = ConnectedProcessStateConstants.IDLE,
    val message: String = "Initializing..."
)

@HiltViewModel
class ConnectedViewModel @Inject constructor() : ViewModel() {

    private val _processStateFlow = MutableStateFlow(ConnectedProcessState())
    val processStateFlow: StateFlow<ConnectedProcessState> = _processStateFlow.asStateFlow()

    private val _connectionInfo = MutableStateFlow(EndpointConnectionInformation())
    val connectionInfo: StateFlow<EndpointConnectionInformation> = _connectionInfo

    private lateinit var connectionsClient: ConnectionsClient

    fun provideConnectionsClient(connectionsClient: ConnectionsClient) {
        this.connectionsClient = connectionsClient
    }

    val messages = mutableStateListOf<String>()
//        private set

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.d("SNDVMPTU", "Payload received ${payload.id}")
            if (payload.type == Payload.Type.BYTES) {
//                payloadReceived(endpointId, payload)
//                val receivedProcessName = decodePayloadToString(payload)
//                Log.d("INDVMPC", "Process received: $receivedProcessName")
                // TODO add it to the database
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            Log.d("SNDVMPTU", "Payload Transfer Update: ${update.status}")
            when (update.status) {
                PayloadTransferUpdate.Status.CANCELED -> {
                    Log.d("INDPLCLBTU", "Transfer cancelled")
                }

                PayloadTransferUpdate.Status.FAILURE -> {
                    Log.d("INDPLCLBTU", "Transfer failed")
                }

                PayloadTransferUpdate.Status.IN_PROGRESS -> {
                    Log.d("INDPLCLBTU", "Transfer in progress")
                }

                PayloadTransferUpdate.Status.SUCCESS -> {
                    Log.d("INDPLCLBTU", "Transfer successful")
                }
            }
        }
    }

    private val timerLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(
            endpointId: String,
            connectionInfo: ConnectionInfo
        ) {
            Log.d(
                "SNDVMCLC",
                "onConnectionInitiated ${connectionInfo.endpointName} / ${connectionInfo.authenticationDigits}"
            )
            // authenticate
            _connectionInfo.value = EndpointConnectionInformation(
                endpointId = endpointId,
                endpointName = connectionInfo.endpointName,
                authenticationDigits = connectionInfo.authenticationDigits
            )
            // now move to auth requested
            _processStateFlow.value = ConnectedProcessState(
                ConnectedProcessStateConstants.AUTHENTICATION_REQUESTED,
                connectionInfo.endpointName
            )
        }

        override fun onConnectionResult(
            endpointId: String,
            connectionResolution: ConnectionResolution
        ) {
            Log.d(
                "SNDVMCLC",
                "onConnectionResult $endpointId: $connectionResolution"
            )
            if (!connectionResolution.status.isSuccess) {
                // failed
                var statusMessage = connectionResolution.status.statusMessage
                if (null == statusMessage) {
                    statusMessage = "Unknown issue"
                }
                _processStateFlow.value = ConnectedProcessState(
                    ConnectedProcessStateConstants.CONNECTION_FAILED,
                    message = statusMessage
                )
            } else {
                // this worked!
                // remember this endpoint! TODO
                _processStateFlow.value = ConnectedProcessState(
                    ConnectedProcessStateConstants.CONNECTION_ESTABLISHED,
                    "Connection established: $endpointId"
                )
            }
        }

        override fun onDisconnected(endpointId: String) {
            _processStateFlow.value =
                ConnectedProcessState(ConnectedProcessStateConstants.DONE, "Disconnected")
        }
    }

    override fun onCleared() {
        // stop all connections when the ViewModel is destroyed
        connectionsClient.stopAdvertising()
        connectionsClient.stopAllEndpoints()
        super.onCleared()
    }

    fun postMessage(message: String) {
        messages.add(message)
    }

    fun transitionToNewState(
        newState: ConnectedProcessStateConstants,
        message: String = "OK"
    ) {
        // there is a lot of logic to do here!
        // DO NOT USE RECURSIVELY!
        when (newState) {
            ConnectedProcessStateConstants.IDLE -> {}

            ConnectedProcessStateConstants.AWAITING_PERMISSIONS -> {
                postMessage("If you would like to receive processes from your phone, this app needs permission for Bluetooth, WiFi, and the discovery of nearby devices, which may also need location permissions.")
                _processStateFlow.value = ConnectedProcessState(newState, "OK")
            }

            ConnectedProcessStateConstants.PERMISSIONS_GRANTED -> {
                postMessage("Permissions granted.")
                _processStateFlow.value = ConnectedProcessState(newState, "OK")
            }

            ConnectedProcessStateConstants.PERMISSIONS_DENIED -> {
                postMessage("Permissions denied.")
                _processStateFlow.value = ConnectedProcessState(newState, "Denied: $message")
            }

            ConnectedProcessStateConstants.CANCELLED -> {
                postMessage("Cancelling...")
                Log.d("INDVM", "Cancelling...")
                // just in case...
                connectionsClient.stopAllEndpoints()
                connectionsClient.stopAdvertising()
                _processStateFlow.value =
                    ConnectedProcessState(ConnectedProcessStateConstants.CANCELLED, "Cancelled")
            }

            ConnectedProcessStateConstants.START_ADVERTISING -> {
                postMessage("Will advertise TV's presence...")
                // trigger the actual advertising
                val advertisingOptions: AdvertisingOptions =
                    AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT)
                        .build()
                Log.d("INDVM", "Starting to advertise... $endpointId")
                _processStateFlow.value = ConnectedProcessState(newState, "OK")
                connectionsClient.startAdvertising(
                    userName,
                    endpointId,
                    timerLifecycleCallback,
                    advertisingOptions
                ).addOnSuccessListener {
                    postMessage("Advertising started.")
                    Log.d("STARTADV", "Advertising started")
                    _processStateFlow.value = ConnectedProcessState(newState, "Advertising")
                    Log.d("ADVSTARTED", "Advertising started and state set")
                }.addOnFailureListener { e: Exception? ->
                    Log.d("STARTADV", "Start of adv failed $e")
                    val errorMessage = "Error starting advertising" + if (e != null) {
                        ": ${e.message}"
                    } else {
                        ""
                    }
                    postMessage(errorMessage)
                    Log.d("STARTADV", errorMessage)
                    connectionsClient.stopAllEndpoints()
                    connectionsClient.stopAdvertising()
                    _processStateFlow.value = ConnectedProcessState(newState, errorMessage)
                    Log.d("ADVSTARTED", "Error: $errorMessage")
                }
            }

            ConnectedProcessStateConstants.ADVERTISING -> {
                postMessage("Now advertising the TV's presence.")
                Log.d("INDVM", "Now advertising...")
                _processStateFlow.value = ConnectedProcessState(newState, "Advertising")
            }

            ConnectedProcessStateConstants.ERROR -> {
                connectionsClient.stopAllEndpoints()
                connectionsClient.stopAdvertising()
                postMessage("Error: ${_processStateFlow.value.message}")
                Log.d("INDVM", "Error: ${_processStateFlow.value.message}")
            }

            ConnectedProcessStateConstants.DISCOVERED -> {
                connectionsClient.stopAdvertising()
                postMessage("The TV has been discovered.")
                _processStateFlow.value = ConnectedProcessState(newState, "OK")
            }

            ConnectedProcessStateConstants.CONNECTION_INITIATED -> {
                postMessage("Connection initiated.")
                _processStateFlow.value = ConnectedProcessState(newState, "Connection initiated")
            }

            ConnectedProcessStateConstants.CONNECTION_ESTABLISHED -> {
                postMessage("Connection established.")
                _processStateFlow.value = ConnectedProcessState(newState, "Connection established")
            }

            ConnectedProcessStateConstants.CONNECTION_FAILED -> {
                postMessage("Connection failed.")
                _processStateFlow.value = ConnectedProcessState(newState, "Connection failed")
            }

            ConnectedProcessStateConstants.AUTHENTICATION_REQUESTED -> {
                postMessage("Authentication requested.")
                _processStateFlow.value = ConnectedProcessState(newState, "Auth requested")
                Log.d("INDVM", "Authentication requested...")
            }

            ConnectedProcessStateConstants.AUTHENTICATION_OK -> {
                postMessage("Authentication OK")
                Log.d("INDVM", "Now accepting the connection...")
                connectionsClient.acceptConnection(connectionInfo.value.endpointId, payloadCallback)
            }

            ConnectedProcessStateConstants.AUTHENTICATION_DENIED -> {
                postMessage("Authentication denied.")
                Log.d("INDVM", "Connection denied!")
                _processStateFlow.value =
                    ConnectedProcessState(
                        ConnectedProcessStateConstants.DISCOVERED,
                        "Connection denied"
                    )
            }

            ConnectedProcessStateConstants.RECEIVING -> {
                // TODO
                _processStateFlow.value =
                    ConnectedProcessState(ConnectedProcessStateConstants.RECEIVING, message)
            }

            ConnectedProcessStateConstants.DONE -> {
                postMessage("Done")
                Log.d("INDVM", "Done")
                // just in case...
                connectionsClient.stopAllEndpoints()
                connectionsClient.stopAdvertising()
                _processStateFlow.value =
                    ConnectedProcessState(ConnectedProcessStateConstants.DONE, "Done")
            }
        }
    }

    fun payloadReceived(endpointId: String, payload: Payload) {
        if (payload.type == Payload.Type.BYTES) {
            val process = payload.toTimerProcess()
            Log.d("INBVM", "Payload received from $endpointId: ${process.name} / $process")
//            _receivedProcesses.add(process)
            transitionToNewState(ConnectedProcessStateConstants.RECEIVING, process.uuid)
        } else {
            Log.d("INBVM", "Payload received from $endpointId but wrong type: $payload")
        }
    }


}