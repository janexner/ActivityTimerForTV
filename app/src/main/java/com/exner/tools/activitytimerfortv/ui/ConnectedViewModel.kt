package com.exner.tools.activitytimerfortv.ui

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.activitytimerfortv.data.AllDataHolder
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.AdvertisingOptions.*
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ConnectedProcessStateConstants {
    IDLE,
    START_ADVERTISING,
    ADVERTISING,
    DISCOVERED,
    CONNECTION_INITIATED,
    CONNECTION_ESTABLISHED,
    CONNECTION_FAILED,
    AUTHENTICATION_REQUESTED,
    AUTHENTICATION_OK,
    AUTHENTICATION_DENIED,
    RECEIVING_DATA,
    SENDING_DATA,
    DONE,
    CANCELLED,
    ERROR
}

const val endpointIdCompanion = "com.exner.tools.ActivityTimer.Companion"
//const val userName = "Activity Timer for TV"

data class ConnectedProcessState(
    val currentState: ConnectedProcessStateConstants = ConnectedProcessStateConstants.IDLE,
    val message: String = "Initializing..."
)

@HiltViewModel
class ConnectedViewModel @Inject constructor(
    val repository: TimerDataRepository
) : ViewModel() {

    private val _processStateFlow = MutableStateFlow(ConnectedProcessState())
    val processStateFlow: StateFlow<ConnectedProcessState> = _processStateFlow.asStateFlow()

    private val _connectionInfo = MutableStateFlow(EndpointConnectionInformation())
    val connectionInfo: StateFlow<EndpointConnectionInformation> = _connectionInfo

    private lateinit var connectionsClient: ConnectionsClient

    fun provideConnectionsClient(connectionsClient: ConnectionsClient) {
        this.connectionsClient = connectionsClient
    }

    val messages = mutableStateListOf<String>()

    private val moshi: Moshi = Moshi.Builder().build()
    val adapter: JsonAdapter<AllDataHolder> = moshi.adapter(AllDataHolder::class.java)

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
            result: ConnectionResolution
        ) {
            Log.d(
                "SNDVMCLC",
                "onConnectionResult $endpointId: $result"
            )
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    // We're connected! Can now start sending and receiving data.
//                     postMessage("Connection established.")
                    transitionToNewState(ConnectedProcessStateConstants.CONNECTION_ESTABLISHED)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    // The connection was rejected by one or both sides.
                    postMessage("Connection rejected.")
                    transitionToNewState(ConnectedProcessStateConstants.CONNECTION_FAILED)
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    // The connection broke before it was able to be accepted.
                    postMessage("Connection failed.")
                    transitionToNewState(ConnectedProcessStateConstants.CONNECTION_FAILED)
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            postMessage("Disconnected")
            transitionToNewState(ConnectedProcessStateConstants.DONE)
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
            ConnectedProcessStateConstants.IDLE -> {
                postMessage("Permissions granted.")
                _processStateFlow.value = ConnectedProcessState(newState, "OK")
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
                    Builder().setStrategy(Strategy.P2P_POINT_TO_POINT)
                        .build()
                Log.d("INDVM", "Starting to advertise... $endpointIdCompanion")
                _processStateFlow.value = ConnectedProcessState(newState, "OK")
                connectionsClient.startAdvertising(
                    userName,
                    endpointIdCompanion,
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
                connectionsClient.stopAdvertising()
                postMessage("Connection initiated.")
                _processStateFlow.value = ConnectedProcessState(newState, "Connection initiated")
            }

            ConnectedProcessStateConstants.CONNECTION_ESTABLISHED -> {
                connectionsClient.stopAdvertising()
                postMessage("Connection established.")
                _processStateFlow.value = ConnectedProcessState(newState, "Connection established")
                postMessage("Preparing data to send...")
                viewModelScope.launch {
                    val allProcesses = repository.getAllProcesses()
                    val allCategories = repository.getAllCategories()
                    val allData = AllDataHolder(processes = allProcesses, categories = allCategories)
                    val allDataJson = adapter.toJson(allData)

                    if (allDataJson.length > ConnectionsClient.MAX_BYTES_DATA_SIZE) {
                        postMessage("Too many processes, cannot send them all!")
                        _processStateFlow.value = ConnectedProcessState(
                            ConnectedProcessStateConstants.ERROR, "Too much data to send!")
                    } else {
                        val payload = Payload.fromBytes(allDataJson.toByteArray(charset = Charsets.UTF_8))
                        postMessage("Sending data (${allDataJson.length} bytes)...")
                        connectionsClient.sendPayload(connectionInfo.value.endpointId, payload)
                        _processStateFlow.value = ConnectedProcessState(
                            ConnectedProcessStateConstants.SENDING_DATA, "Sending data (${allDataJson.length} bytes)..."
                        )
                    }
                }
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
                connectionsClient.stopAdvertising()
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

            ConnectedProcessStateConstants.DONE -> {
                postMessage("Done")
                Log.d("INDVM", "Done")
                // just in case...
                connectionsClient.stopAllEndpoints()
                connectionsClient.stopAdvertising()
                _processStateFlow.value =
                    ConnectedProcessState(ConnectedProcessStateConstants.DONE, "Done")
            }

            ConnectedProcessStateConstants.RECEIVING_DATA -> {
                postMessage("Receiving data...")
            }
            ConnectedProcessStateConstants.SENDING_DATA -> {
                postMessage("Sending data...")
            }
        }
    }

    fun payloadReceived(endpointId: String, payload: Payload) {
        if (payload.type == Payload.Type.BYTES) {
            val process = payload.toTimerProcess()
            Log.d("INBVM", "Payload received from $endpointId: ${process.name} / $process")
//            _receivedProcesses.add(process)
            transitionToNewState(ConnectedProcessStateConstants.RECEIVING_DATA, process.uuid)
        } else {
            Log.d("INBVM", "Payload received from $endpointId but wrong type: $payload")
        }
    }


}