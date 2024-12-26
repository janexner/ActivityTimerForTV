package com.exner.tools.activitytimerfortv.ui

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
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

@HiltViewModel
class ConnectedViewModel @Inject constructor() : ViewModel() {

    private val _processStateFlow = MutableStateFlow(ProcessState())
    val processStateFlow: StateFlow<ProcessState> = _processStateFlow.asStateFlow()

    private val _connectionInfo = MutableStateFlow(EndpointConnectionInformation())
    val connectionInfo: StateFlow<EndpointConnectionInformation> = _connectionInfo

    private lateinit var connectionsClient: ConnectionsClient

    fun provideConnectionsClient(connectionsClient: ConnectionsClient) {
        this.connectionsClient = connectionsClient
    }

    private val _messages: MutableLiveData<List<String>> = MutableLiveData<List<String>>(listOf())
    val messages: LiveData<List<String>> = _messages

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
            _processStateFlow.value = ProcessState(
                ProcessStateConstants.AUTHENTICATION_REQUESTED,
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
                _processStateFlow.value = ProcessState(
                    ProcessStateConstants.CONNECTION_FAILED,
                    message = statusMessage
                )
            } else {
                // this worked!
                // remember this endpoint! TODO
                _processStateFlow.value = ProcessState(
                    ProcessStateConstants.CONNECTION_ESTABLISHED,
                    "Connection established: $endpointId"
                )
            }
        }

        override fun onDisconnected(endpointId: String) {
            _processStateFlow.value = ProcessState(ProcessStateConstants.DONE, "Disconnected")
        }
    }

    override fun onCleared() {
        // stop all connections when the ViewModel is destroyed
        connectionsClient.stopAdvertising()
        connectionsClient.stopAllEndpoints()
        super.onCleared()
    }

    fun transitionToNewState(
        newState: ProcessStateConstants,
        message: String = "OK"
    ) {
        // there is a lot of logic to do here!
        // DO NOT USE RECURSIVELY!
        when (newState) {
            ProcessStateConstants.PERMISSIONS_GRANTED -> {
                _processStateFlow.value = ProcessState(newState, "OK")
            }

            ProcessStateConstants.PERMISSIONS_DENIED -> {
                _processStateFlow.value = ProcessState(newState, "Denied: $message")
            }

            ProcessStateConstants.CANCELLED -> {
                Log.d("INDVM", "Cancelling...")
                // just in case...
                connectionsClient.stopAllEndpoints()
                connectionsClient.stopAdvertising()
                _processStateFlow.value = ProcessState(ProcessStateConstants.CANCELLED, "Cancelled")
            }

            ProcessStateConstants.START_ADVERTISING -> {
                // trigger the actual advertising
                val advertisingOptions: AdvertisingOptions =
                    AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT)
                        .build()
                Log.d("INDVM", "Starting to advertise... $endpointId")
                _processStateFlow.value = ProcessState(newState, "OK")
                connectionsClient.startAdvertising(
                    userName,
                    endpointId,
                    timerLifecycleCallback,
                    advertisingOptions
                ).addOnSuccessListener {
                    Log.d("STARTADV", "Advertising started")
                    _processStateFlow.value = ProcessState(newState, "Advertising")
                    Log.d("ADVSTARTED", "Advertising started and state set")
                }.addOnFailureListener { e: Exception? ->
                    Log.d("STARTADV", "Start of adv failed $e")
                    val errorMessage = "Error starting advertising" + if (e != null) {
                        ": ${e.message}"
                    } else {
                        ""
                    }
                    Log.d("STARTADV", errorMessage)
                    connectionsClient.stopAllEndpoints()
                    connectionsClient.stopAdvertising()
                    _processStateFlow.value = ProcessState(newState, errorMessage)
                    Log.d("ADVSTARTED", "Error: $errorMessage")
                }
            }

            ProcessStateConstants.AWAITING_PERMISSIONS -> {
                _processStateFlow.value = ProcessState(newState, "OK")
            }

            ProcessStateConstants.ADVERTISING -> {
                Log.d("INDVM", "Now advertising...")
                _processStateFlow.value = ProcessState(newState, "Advertising")
            }

            ProcessStateConstants.ERROR -> {
                connectionsClient.stopAllEndpoints()
                connectionsClient.stopAdvertising()
                Log.d("INDVM", "Error: ${_processStateFlow.value.message}")
            }

            ProcessStateConstants.DISCOVERED -> {
                connectionsClient.stopAdvertising()
                _processStateFlow.value = ProcessState(newState, "OK")
            }

            ProcessStateConstants.CONNECTION_INITIATED -> {
                _processStateFlow.value = ProcessState(newState, "Connection initiated")
            }

            ProcessStateConstants.CONNECTION_ESTABLISHED -> {
                _processStateFlow.value = ProcessState(newState, "Connection established")
            }

            ProcessStateConstants.CONNECTION_FAILED -> {
                _processStateFlow.value = ProcessState(newState, "Connection failed")
            }

            ProcessStateConstants.AUTHENTICATION_REQUESTED -> {
                _processStateFlow.value = ProcessState(newState, "Auth requested")
                Log.d("INDVM", "Authentication requested...")
            }

            ProcessStateConstants.AUTHENTICATION_OK -> {
                Log.d("INDVM", "Now accepting the connection...")
                connectionsClient.acceptConnection(connectionInfo.value.endpointId, payloadCallback)
            }

            ProcessStateConstants.AUTHENTICATION_DENIED -> {
                Log.d("INDVM", "Connection denied!")
                _processStateFlow.value =
                    ProcessState(ProcessStateConstants.DISCOVERED, "Connection denied")
            }

            ProcessStateConstants.RECEIVING -> {
                _processStateFlow.value = ProcessState(ProcessStateConstants.RECEIVING, message)
            }

            ProcessStateConstants.DONE -> {
                Log.d("INDVM", "Done")
                // just in case...
                connectionsClient.stopAllEndpoints()
                connectionsClient.stopAdvertising()
                _processStateFlow.value = ProcessState(ProcessStateConstants.DONE, "Done")
            }
        }
    }

    fun payloadReceived(endpointId: String, payload: Payload) {
        if (payload.type == Payload.Type.BYTES) {
            val process = payload.toTimerProcess()
            Log.d("INBVM", "Payload received from $endpointId: ${process.name} / $process")
//            _receivedProcesses.add(process)
            transitionToNewState(ProcessStateConstants.RECEIVING, process.uuid)
        } else {
            Log.d("INBVM", "Payload received from $endpointId but wrong type: $payload")
        }
    }


}