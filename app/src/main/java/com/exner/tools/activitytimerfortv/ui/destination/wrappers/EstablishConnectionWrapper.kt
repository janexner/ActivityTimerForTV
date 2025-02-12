package com.exner.tools.activitytimerfortv.ui.destination.wrappers

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.ui.EndpointConnectionInformation
import com.exner.tools.activitytimerfortv.ui.MainActivityViewModel
import com.exner.tools.activitytimerfortv.ui.tools.DefaultSpacer
import com.exner.tools.activitytimerfortv.ui.tools.IconSpacer
import com.exner.tools.activitytimerfortv.ui.tools.StandardButton
import com.exner.tools.activitytimerfortv.ui.tools.StandardDialog
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.AdvertisingOptions.Builder
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.wrapper.DestinationWrapper

enum class ConnectedProcessStateConstants {
    IDLE,
    START_ADVERTISING,
    ADVERTISING,
    CONNECTION_FAILED,
    AUTHENTICATION_REQUESTED,
    AUTHENTICATION_OK,
    AUTHENTICATION_DENIED,
    DONE,
    CANCELLED,
    ERROR
}

private val STRATEGY = Strategy.P2P_POINT_TO_POINT
private const val SERVICE_ID_COMPANION = "com.exner.tools.ActivityTimer.Companion"
private const val USER_NAME = "Activity Timer Companion"
private const val TAG = "ECW"

object EstablishConnectionWrapper : DestinationWrapper {

    @Composable
    override fun <T> DestinationScope<T>.Wrap(screenContent: @Composable () -> Unit) {

        val mainActivityViewModel = hiltViewModel<MainActivityViewModel>()
        val companionConnectionState = mainActivityViewModel.companionConnectionStateHolder.companionConnectionState.collectAsState()

        val currentState = mainActivityViewModel.connectionUIState.collectAsState()

        val connectionInformation = remember { mutableStateOf(EndpointConnectionInformation()) }

        // for the actual advertising
        val context = LocalContext.current
        val connectionsClient = Nearby.getConnectionsClient(context)

        val timerLifecycleCallback = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(
                endpointId: String,
                connectionInfo: ConnectionInfo
            ) {
                Log.d(
                    TAG,
                    "onConnectionInitiated ${connectionInfo.endpointName} / ${connectionInfo.authenticationDigits}"
                )
                // authenticate
                connectionInformation.value = EndpointConnectionInformation(
                    endpointId = endpointId,
                    endpointName = connectionInfo.endpointName,
                    authenticationDigits = connectionInfo.authenticationDigits
                )
                // now move to auth requested
                mainActivityViewModel.updateConnectionUIState(ConnectedProcessStateConstants.AUTHENTICATION_REQUESTED)
            }

            override fun onConnectionResult(
                endpointId: String,
                result: ConnectionResolution
            ) {
                Log.d(
                    TAG,
                    "onConnectionResult $endpointId: $result"
                )
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        // We're connected! Can now start sending and receiving data.
                        mainActivityViewModel.updateConnectedToCompanion(isConnected = true)
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        // The connection was rejected by one or both sides.
                        mainActivityViewModel.updateConnectionUIState(ConnectedProcessStateConstants.CONNECTION_FAILED)
                        mainActivityViewModel.updateConnectedToCompanion(isConnected = false)
                    }
                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        // The connection broke before it was able to be accepted.
                        mainActivityViewModel.updateConnectionUIState(ConnectedProcessStateConstants.CONNECTION_FAILED)
                        mainActivityViewModel.updateConnectedToCompanion(isConnected = false)
                    }
                }
            }

            override fun onDisconnected(endpointId: String) {
                mainActivityViewModel.updateConnectionUIState(ConnectedProcessStateConstants.DONE)
                mainActivityViewModel.updateConnectedToCompanion(isConnected = false)
            }
        }

        val payloadCallback = object : PayloadCallback() {
            override fun onPayloadReceived(endpointId: String, payload: Payload) {
                Log.d(TAG, "Payload received ${payload.id}")
                if (payload.type == Payload.Type.BYTES) {
                    Log.d(TAG, "Correct payload type ${payload.type}")
                    // TODO add it to the database
                }
            }

            override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
                Log.d(TAG, "Payload Transfer Update: ${update.status}")
                when (update.status) {
                    PayloadTransferUpdate.Status.CANCELED -> {
                        Log.d(TAG, "Transfer cancelled")
                    }

                    PayloadTransferUpdate.Status.FAILURE -> {
                        Log.d(TAG, "Transfer failed")
                    }

                    PayloadTransferUpdate.Status.IN_PROGRESS -> {
                        Log.d(TAG, "Transfer in progress")
                    }

                    PayloadTransferUpdate.Status.SUCCESS -> {
                        Log.d(TAG, "Transfer successful")
                    }
                }
            }
        }

        if (companionConnectionState.value.isConnectedToCompanion) {
            screenContent()
        } else {
            // we have to advertise and connect
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp, vertical = 24.dp)
            ) {
                // buttons
                Row {
                    when (currentState.value.currentState) {
                        ConnectedProcessStateConstants.IDLE,
                        ConnectedProcessStateConstants.CANCELLED,
                        ConnectedProcessStateConstants.DONE -> {
                            Button(
                                onClick = {
                                    // reset
                                    connectionsClient.stopAllEndpoints()
                                    connectionsClient.stopAdvertising()
                                    // trigger the actual advertising
                                    val advertisingOptions: AdvertisingOptions =
                                        Builder().setStrategy(STRATEGY).build()
                                    Log.d(TAG, "Starting to advertise... $SERVICE_ID_COMPANION")
                                    connectionsClient.startAdvertising(
                                        USER_NAME,
                                        SERVICE_ID_COMPANION,
                                        timerLifecycleCallback,
                                        advertisingOptions
                                    ).addOnSuccessListener {
                                        Log.d(TAG, "Advertising started")
                                        mainActivityViewModel.updateConnectionUIState(ConnectedProcessStateConstants.ADVERTISING)
                                        Log.d(TAG, "Advertising started and state set")
                                    }.addOnFailureListener { e: Exception? ->
                                        Log.d(TAG, "Start of adv failed $e")
                                        val errorMessage = "Error starting advertising" + if (e != null) {
                                            ": ${e.message}"
                                        } else {
                                            ""
                                        }
                                        Log.d(TAG, errorMessage)
                                        connectionsClient.stopAllEndpoints()
                                        connectionsClient.stopAdvertising()
                                        mainActivityViewModel.updateConnectionUIState(ConnectedProcessStateConstants.ERROR)
                                        Log.d(TAG, "Error: $errorMessage")
                                    }
                                    mainActivityViewModel.updateConnectionUIState(
                                        ConnectedProcessStateConstants.START_ADVERTISING
                                    )
                                },
                                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Start advertising",
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                IconSpacer()
                                Text(text = "Start advertising")
                            }
                        }

                        else -> {}
                    }
                    Spacer(modifier = Modifier.weight(0.9f))
                    if (currentState.value.currentState == ConnectedProcessStateConstants.CANCELLED || currentState.value.currentState == ConnectedProcessStateConstants.DONE) {
                        StandardButton(
                            onClick = {
                                destinationsNavigator.navigateUp()
                            },
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            text = "Go back"
                        )
                    } else {
                        StandardButton(
                            onClick = {
                                mainActivityViewModel.updateConnectionUIState(
                                    ConnectedProcessStateConstants.CANCELLED
                                )
                            },
                            imageVector = Icons.Default.Clear,
                            text = "Cancel"
                        )
                    }
                }
                // spacer
                DefaultSpacer()

                if (currentState.value.currentState == ConnectedProcessStateConstants.AUTHENTICATION_REQUESTED) {
                    val openAuthenticationDialog = remember { mutableStateOf(false) }
                    openAuthenticationDialog.value = true
                    AuthenticationRequestedScreen(
                        openAuthenticationDialog = openAuthenticationDialog.value,
                        info = connectionInformation.value,
                        confirmCallback = {
                            openAuthenticationDialog.value = false
                            connectionsClient.acceptConnection(connectionInformation.value.endpointId, payloadCallback)
                            mainActivityViewModel.updateConnectionUIState(
                                ConnectedProcessStateConstants.AUTHENTICATION_OK
                            )
                        },
                        dismissCallback = {
                            openAuthenticationDialog.value = false
                            mainActivityViewModel.updateConnectionUIState(
                                ConnectedProcessStateConstants.AUTHENTICATION_DENIED
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun AuthenticationRequestedScreen(
    openAuthenticationDialog: Boolean,
    info: EndpointConnectionInformation,
    confirmCallback: () -> Unit,
    dismissCallback: () -> Unit
) {
    StandardDialog(
        showDialog = openAuthenticationDialog,
        title = { Text(text = "Accept connection to " + info.endpointName) },
        text = { Text(text = "Confirm the code matches on both devices: " + info.authenticationDigits) },
        icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = "Alert") },
        onDismissRequest = { dismissCallback() },
        confirmButton = {
            Button(onClick = { confirmCallback() }) {
                Text(text = "Accept")
            }
        },
        dismissButton = {
            Button(onClick = { dismissCallback() }) {
                Text(text = "Decline")
            }
        }
    )
}