package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Checkbox
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.exner.tools.activitytimerfortv.network.Permissions
import com.exner.tools.activitytimerfortv.ui.EndpointConnectionInformation
import com.exner.tools.activitytimerfortv.ui.ImportFromNearbyDeviceViewModel
import com.exner.tools.activitytimerfortv.ui.ProcessStateConstants
import com.exner.tools.activitytimerfortv.ui.tools.ProcessCard
import com.exner.tools.activitytimerfortv.ui.tools.StandardDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.nearby.Nearby
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@OptIn(ExperimentalPermissionsApi::class)
@Destination<RootGraph>
@Composable
fun ImportFromNearbyDevice(
    importFromNearbyDeviceViewModel: ImportFromNearbyDeviceViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val permissions = Permissions(context = context)
    val permissionsNeeded =
        rememberMultiplePermissionsState(permissions = permissions.getAllNecessaryPermissionsAsListOfStrings())

    val processState by importFromNearbyDeviceViewModel.processStateFlow.collectAsState()

    importFromNearbyDeviceViewModel.provideConnectionsClient(Nearby.getConnectionsClient(context))

    val openAuthenticationDialog = remember { mutableStateOf(false) }
    val connectionInfo by importFromNearbyDeviceViewModel.connectionInfo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp, 12.dp)
    ) {
        // buttons
        Row {
            when (processState.currentState) {
                ProcessStateConstants.AWAITING_PERMISSIONS,
                ProcessStateConstants.PERMISSIONS_DENIED -> {
                    Button(
                        enabled = !permissionsNeeded.allPermissionsGranted,
                        onClick = {
                            permissionsNeeded.launchMultiplePermissionRequest()
                        },
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Request permissions",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = "Request permissions")
                    }
                }

                ProcessStateConstants.PERMISSIONS_GRANTED,
                ProcessStateConstants.CANCELLED,
                ProcessStateConstants.DONE -> {
                    if (importFromNearbyDeviceViewModel.receivedProcesses.isEmpty()) {
                        Button(
                            enabled = permissionsNeeded.allPermissionsGranted,
                            onClick = {
                                importFromNearbyDeviceViewModel.transitionToNewState(
                                    ProcessStateConstants.START_ADVERTISING
                                )
                            },
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Call,
                                contentDescription = "Start advertising",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = "Start advertising")
                        }
                    }
                }

                ProcessStateConstants.RECEIVING,
                ProcessStateConstants.CANCELLED,
                ProcessStateConstants.DONE -> {
                    if (importFromNearbyDeviceViewModel.receivedProcesses.isNotEmpty()) {
                        Button(
                            enabled = true,
                            onClick = { /*TODO*/ },
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add selected",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = "Add selected")
                        }
                    }
                }

                else -> {}
            }
            Spacer(modifier = Modifier.weight(0.5f))
            Button(
                enabled = true,
                onClick = {
                    if (processState.currentState == ProcessStateConstants.CANCELLED) {
                        navigator.navigateUp()
                    } else {
                        importFromNearbyDeviceViewModel.transitionToNewState(
                            ProcessStateConstants.CANCELLED,
                            "Cancelled by user"
                        )
                    }
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Cancel",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "Cancel")
            }
        }
        // spacer
        Spacer(modifier = Modifier.weight(0.1f))

        // some sanity checking for state
        if (processState.currentState == ProcessStateConstants.AWAITING_PERMISSIONS && permissionsNeeded.allPermissionsGranted) {
            importFromNearbyDeviceViewModel.transitionToNewState(ProcessStateConstants.PERMISSIONS_GRANTED)
        }

        // display received processes
        if (importFromNearbyDeviceViewModel.receivedProcesses.isNotEmpty()) {
            Text(text = "Processes received. Select process to import it.")
            Spacer(modifier = Modifier.size(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 250.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(importFromNearbyDeviceViewModel.receivedProcesses) { process ->
                    Box(modifier = Modifier.padding(PaddingValues(8.dp))) {
                        ProcessCard(
                            process = process,
                            backgroundUriFallback = null,
                            onClick = {
                                importFromNearbyDeviceViewModel.importProcessIntoLocalDatabase(
                                    process
                                )
                            }
                        )
                    }
                }
            }
            // spacer
            Spacer(modifier = Modifier.weight(0.1f))
        }

        // UI, depending on state
        when (processState.currentState) {
            ProcessStateConstants.AWAITING_PERMISSIONS -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "If you would like to receive processes from your phone, this app needs permission for Bluetooth, WiFi, and the discovery of nearby devices, which may also need location permissions.")
                }
            }

            ProcessStateConstants.PERMISSIONS_GRANTED -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "All permissions OK, ready to advertise.")
                }
            }

            ProcessStateConstants.START_ADVERTISING -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Starting advertising...")
                }
            }

            ProcessStateConstants.PERMISSIONS_DENIED -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Without the necessary permissions, importing from nearby devices is not possible.")
                }
            }

            ProcessStateConstants.ADVERTISING -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Waiting for devices...")
                }
            }

            ProcessStateConstants.DISCOVERED -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "We have been discovered!")
                }
            }

            ProcessStateConstants.CONNECTION_INITIATED -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Connection initiated, waiting for partner to confirm...")
                }
            }

            ProcessStateConstants.CONNECTION_ESTABLISHED -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Connection has been established")
                }
            }

            ProcessStateConstants.CONNECTION_FAILED -> TODO()

            ProcessStateConstants.RECEIVING -> {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item { Text(text = "We are receiving data!") }
                    if (importFromNearbyDeviceViewModel.receivedProcesses.isNotEmpty()) {
                        item { Text(text = "Received ${importFromNearbyDeviceViewModel.receivedProcesses.size} process(es) so far...") }
                    }
                }
            }

            ProcessStateConstants.AUTHENTICATION_REQUESTED -> {
                openAuthenticationDialog.value = true
                ProcessStateAuthenticationRequestedScreen(
                    openAuthenticationDialog = openAuthenticationDialog.value,
                    info = connectionInfo,
                    confirmCallback = {
                        openAuthenticationDialog.value = false
                        importFromNearbyDeviceViewModel.transitionToNewState(
                            ProcessStateConstants.AUTHENTICATION_OK,
                            "Accepted"
                        )
                    },
                    dismissCallback = {
                        openAuthenticationDialog.value = false
                        importFromNearbyDeviceViewModel.transitionToNewState(
                            ProcessStateConstants.AUTHENTICATION_DENIED,
                            "Denied"
                        )
                    }
                )
            }

            ProcessStateConstants.AUTHENTICATION_OK -> {}
            ProcessStateConstants.AUTHENTICATION_DENIED -> {}

            ProcessStateConstants.ERROR -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Some error occurred. It may help to move away from this screen and try it all again.")
                    Text(text = processState.message)
                }
            }

            ProcessStateConstants.DONE,
            ProcessStateConstants.CANCELLED -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Done or Cancelled")
                }
            }
        }
    }
}

@Composable
fun ProcessToImportRow(process: TimerProcess) {
    Row {
        Checkbox(
            checked = false,
            onCheckedChange = {}
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = process.name)
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = "${process.processTime}/${process.intervalTime}")
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = process.info)
        if (process.hasAutoChain) {
            Spacer(modifier = Modifier.size(16.dp))
            Text(text = "-> ${process.gotoName}")
        }
    }
}

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun ProcessStateAuthenticationRequestedScreen(
    openAuthenticationDialog: Boolean,
    info: EndpointConnectionInformation,
    confirmCallback: () -> Unit,
    dismissCallback: () -> Unit
) {
    if (openAuthenticationDialog) {
        StandardDialog(
            showDialog = openAuthenticationDialog,
            title = { Text(text = "Accept connection to " + info.connectionInfo.endpointName) },
            text = { Text(text = "Confirm the code matches on both devices: " + info.connectionInfo.authenticationDigits) },
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
}