package com.exner.tools.activitytimerfortv.ui

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.exner.tools.activitytimerfortv.data.preferences.UserPreferencesManager
import com.exner.tools.activitytimerfortv.ui.tools.CategoryListDefinitions
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.text.Charsets.UTF_8

enum class ImportProcessStateConstants {
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
    RECEIVING,
    DONE,
    CANCELLED,
    ERROR
}

const val endpointId = "com.exner.tools.ActivityTimer"
const val userName = "Activity Timer for TV"

data class ImportProcessState(
    val currentState: ImportProcessStateConstants = ImportProcessStateConstants.IDLE,
    val message: String = "Idle"
)

data class EndpointConnectionInformation(
    val endpointId: String = "",
    val endpointName: String = "",
    val authenticationDigits: String = ""
)

@HiltViewModel
class ImportFromNearbyDeviceViewModel @Inject constructor(
    val repository: TimerDataRepository,
    val userPreferencesRepository: UserPreferencesManager
) : ViewModel() {
    private val _processStateFlow = MutableStateFlow(ImportProcessState())
    val processStateFlow: StateFlow<ImportProcessState> = _processStateFlow.asStateFlow()

    private val _connectionInfo = MutableStateFlow(EndpointConnectionInformation())
    val connectionInfo: StateFlow<EndpointConnectionInformation> = _connectionInfo

    private lateinit var connectionsClient: ConnectionsClient

    fun provideConnectionsClient(connectionsClient: ConnectionsClient) {
        this.connectionsClient = connectionsClient
    }

    private val _receivedProcesses = mutableStateListOf<TimerProcess>()
    val receivedProcesses: List<TimerProcess> = _receivedProcesses

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.d("SNDVMPTU", "Payload received ${payload.id}")
            if (payload.type == Payload.Type.BYTES) {
                payloadReceived(endpointId, payload)
                val receivedProcessName = decodePayloadToString(payload)
                Log.d("INDVMPC", "Process received: $receivedProcessName")
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
            _processStateFlow.value = ImportProcessState(
                ImportProcessStateConstants.AUTHENTICATION_REQUESTED,
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
                    transitionToNewState(ImportProcessStateConstants.CONNECTION_ESTABLISHED)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    // The connection was rejected by one or both sides.
                    transitionToNewState(ImportProcessStateConstants.CONNECTION_FAILED)
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    // The connection broke before it was able to be accepted.
                    transitionToNewState(ImportProcessStateConstants.CONNECTION_FAILED)
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            _processStateFlow.value = ImportProcessState(ImportProcessStateConstants.DONE, "Disconnected")
        }
    }

    override fun onCleared() {
        // stop all connections when the ViewModel is destroyed
        connectionsClient.stopAdvertising()
        connectionsClient.stopAllEndpoints()
        super.onCleared()
    }

    fun transitionToNewState(
        newState: ImportProcessStateConstants,
        message: String = "OK"
    ) {
        // there is a lot of logic to do here!
        // DO NOT USE RECURSIVELY!
        when (newState) {
            ImportProcessStateConstants.IDLE -> {
                _processStateFlow.value = ImportProcessState(newState, "OK")
            }

            ImportProcessStateConstants.CANCELLED -> {
                Log.d("INDVM", "Cancelling...")
                // just in case...
                connectionsClient.stopAllEndpoints()
                connectionsClient.stopAdvertising()
                _processStateFlow.value = ImportProcessState(ImportProcessStateConstants.CANCELLED, "Cancelled")
            }

            ImportProcessStateConstants.START_ADVERTISING -> {
                // trigger the actual advertising
                val advertisingOptions: AdvertisingOptions =
                    AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT)
                        .build()
                Log.d("INDVM", "Starting to advertise... $endpointId")
                _processStateFlow.value = ImportProcessState(newState, "OK")
                connectionsClient.startAdvertising(
                    userName,
                    endpointId,
                    timerLifecycleCallback,
                    advertisingOptions
                ).addOnSuccessListener {
                    Log.d("STARTADV", "Advertising started")
                    _processStateFlow.value = ImportProcessState(newState, "Advertising")
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
                    _processStateFlow.value = ImportProcessState(newState, errorMessage)
                    Log.d("ADVSTARTED", "Error: $errorMessage")
                }
            }

            ImportProcessStateConstants.ADVERTISING -> {
                Log.d("INDVM", "Now advertising...")
                _processStateFlow.value = ImportProcessState(newState, "Advertising")
            }

            ImportProcessStateConstants.ERROR -> {
                connectionsClient.stopAllEndpoints()
                connectionsClient.stopAdvertising()
                Log.d("INDVM", "Error: ${_processStateFlow.value.message}")
            }

            ImportProcessStateConstants.DISCOVERED -> {
                connectionsClient.stopAdvertising()
                _processStateFlow.value = ImportProcessState(newState, "OK")
            }

            ImportProcessStateConstants.CONNECTION_INITIATED -> {
                _processStateFlow.value = ImportProcessState(newState, "Connection initiated")
            }

            ImportProcessStateConstants.CONNECTION_ESTABLISHED -> {
                _processStateFlow.value = ImportProcessState(newState, "Connection established")
            }

            ImportProcessStateConstants.CONNECTION_FAILED -> {
                _processStateFlow.value = ImportProcessState(newState, "Connection failed")
            }

            ImportProcessStateConstants.AUTHENTICATION_REQUESTED -> {
                _processStateFlow.value = ImportProcessState(newState, "Auth requested")
                Log.d("INDVM", "Authentication requested...")
            }

            ImportProcessStateConstants.AUTHENTICATION_OK -> {
                Log.d("INDVM", "Now accepting the connection...")
                connectionsClient.acceptConnection(connectionInfo.value.endpointId, payloadCallback)
            }

            ImportProcessStateConstants.AUTHENTICATION_DENIED -> {
                Log.d("INDVM", "Connection denied!")
                _processStateFlow.value =
                    ImportProcessState(ImportProcessStateConstants.DISCOVERED, "Connection denied")
            }

            ImportProcessStateConstants.RECEIVING -> {
                _processStateFlow.value = ImportProcessState(ImportProcessStateConstants.RECEIVING, message)
            }

            ImportProcessStateConstants.DONE -> {
                Log.d("INDVM", "Done")
                // just in case...
                connectionsClient.stopAllEndpoints()
                connectionsClient.stopAdvertising()
                _processStateFlow.value = ImportProcessState(ImportProcessStateConstants.DONE, "Done")
            }
        }
    }

    fun payloadReceived(endpointId: String, payload: Payload) {
        if (payload.type == Payload.Type.BYTES) {
            val process = payload.toTimerProcess()
            Log.d("INBVM", "Payload received from $endpointId: ${process.name} / $process")
            _receivedProcesses.add(process)
            transitionToNewState(ImportProcessStateConstants.RECEIVING, process.uuid)
        } else {
            Log.d("INBVM", "Payload received from $endpointId but wrong type: $payload")
        }
    }

    fun doesProcessExistInLocalDatabase(process: TimerProcess): Boolean = runBlocking {
        return@runBlocking (null != this@ImportFromNearbyDeviceViewModel.repository.loadProcessByUuid(
            process.uuid
        ))
    }

    fun importProcessIntoLocalDatabase(process: TimerProcess) {
        Log.d("INDVMSV", "Saving process ${process.name} to database...")
        // add it to the database
        viewModelScope.launch {
            val checkProcess = repository.loadProcessByUuid(process.uuid)
            if (checkProcess != null) {
                Log.e("INDVMSV", "Process ${process.uuid} exists, not overwriting!")
            } else {
                repository.insert(
                    process.copy(
                        uid = 0,
                        categoryId = CategoryListDefinitions.CATEGORY_UID_NONE
                    )
                )
                // remove it from the list of received processes
                _receivedProcesses.remove(process)
            }
        }
    }

    fun updateProcessInLocalDatabase(process: TimerProcess) {
        Log.d("INDVMSV", "Saving process ${process.name} to database...")
        // add it to the database
        viewModelScope.launch {
            val checkProcess = repository.loadProcessByUuid(process.uuid)
            if (checkProcess != null) {
                Log.d("INDVMSV", "Process ${process.uuid} exists, updating...")
                repository.updateProcess(process)
                // remove it from the list of received processes
                _receivedProcesses.remove(process)
            } else {
                Log.e("INDVMSV", "Process ${process.uuid} doesn't exist, cannot update!")
            }
        }
    }

    fun decodePayloadToString(payload: Payload): String {
        if (payload.type == Payload.Type.BYTES) {
            val process = payload.toTimerProcess()
            Log.d("INBVMPD", "Payload received: ${process.name} / $process")
            return process.name
        } else {
            Log.d("INBVMPD", "Payload received but wrong type: $payload")
        }
        return "N/A"
    }

    fun dbgGenerateReceivedProcessesList() {
        val p1 = TimerProcess(
            name = "Process 1",
            info = "The 1st process",
            uuid = "processUuid1",
            processTime = 30,
            intervalTime = 10,
            hasAutoChain = false,
            gotoUuid = null,
            gotoName = null,
            categoryId = CategoryListDefinitions.CATEGORY_UID_NONE,
            backgroundUri = null,
            uid = 1L
        )
        _receivedProcesses.add(p1)
        val p2 = TimerProcess(
            name = "Process 2",
            info = "The 2nd process, leads into the 3rd",
            uuid = "processUuid2",
            processTime = 90,
            intervalTime = 15,
            hasAutoChain = true,
            gotoUuid = "processUuid3",
            gotoName = "Process 3",
            categoryId = CategoryListDefinitions.CATEGORY_UID_NONE,
            backgroundUri = null,
            uid = 2L
        )
        _receivedProcesses.add(p2)
        val p3 = TimerProcess(
            name = "Process 3",
            info = "The 3rd process",
            uuid = "processUuid3",
            processTime = 90,
            intervalTime = 15,
            hasAutoChain = false,
            gotoUuid = null,
            gotoName = null,
            categoryId = CategoryListDefinitions.CATEGORY_UID_NONE,
            backgroundUri = null,
            uid = 3L
        )
        _receivedProcesses.add(p3)
    }
}

fun Payload.toTimerProcess(): TimerProcess {
    val positionStr = String(asBytes()!!, UTF_8)
    Log.d("INDVMPL2FP", "Received $positionStr")
    val positionArray = positionStr.split("|")
    return TimerProcess(
        name = positionArray[0],
        info = positionArray[1],
        uuid = positionArray[2],
        processTime = positionArray[3].toInt(),
        intervalTime = positionArray[4].toInt(),
        hasAutoChain = positionArray[5].toBoolean(),
        gotoUuid = positionArray[6],
        gotoName = positionArray[7],
        categoryId = if (positionArray[8] == "null") 0 else positionArray[8].toLong(),
        backgroundUri = if (positionArray[9] == "null") null else positionArray[8],
        uid = positionArray[10].toLong()
    )
}