package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Icon
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.Text
import androidx.tv.material3.WideButton
import com.exner.tools.activitytimerfortv.network.Permissions
import com.exner.tools.activitytimerfortv.ui.ImportFromNearbyDeviceViewModel
import com.exner.tools.activitytimerfortv.ui.ProcessStateConstants
import com.exner.tools.activitytimerfortv.ui.tools.ActivityTimerNavigationDrawerContent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@OptIn(ExperimentalPermissionsApi::class)
@Destination
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

    NavigationDrawer(drawerContent = {
        ActivityTimerNavigationDrawerContent(
            navigator = navigator,
            defaultSelectedIndex = 2
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp, 12.dp)
        ) {
            // buttons
            Row {
                WideButton(
                    enabled = processState.currentState == ProcessStateConstants.PROCESSES_SELECTED,
                    onClick = {
                        // TODO
                    },
                    title = { Text(text = "Import") },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = "Import"
                        )
                    }
                )
                Spacer(modifier = Modifier.size(24.dp))
                when (processState.currentState) {
                    ProcessStateConstants.PERMISSIONS_GRANTED -> {
                        WideButton(
                            enabled = permissionsNeeded.allPermissionsGranted,
                            onClick = {
                                importFromNearbyDeviceViewModel.transitionToNewState(
                                    ProcessStateConstants.ADVERTISING
                                )
                            },
                            title = { Text(text = "Discover Devices") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Call,
                                    contentDescription = "Discover Devices"
                                )
                            }
                        )
                    }

                    else -> {}
                }
                Spacer(modifier = Modifier.weight(0.5f))
                WideButton(
                    enabled = true,
                    title = { Text(text = "Cancel") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Cancel"
                        )
                    },
                    onClick = {
                        importFromNearbyDeviceViewModel.transitionToNewState(
                            ProcessStateConstants.DONE,
                            "Cancelled by user"
                        )
                    }
                )
            }
            // spacer
            Spacer(modifier = Modifier.weight(0.1f))

            // some sanity checking for state
            if (processState.currentState == ProcessStateConstants.AWAITING_PERMISSIONS && permissionsNeeded.allPermissionsGranted) {
                importFromNearbyDeviceViewModel.transitionToNewState(ProcessStateConstants.PERMISSIONS_GRANTED)
            }

            // UI, depending on state
            when (processState.currentState) {
                ProcessStateConstants.AWAITING_PERMISSIONS -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "If you would like to receive processes from your phone, this app needs permission for Bluetooth, WiFi, and the discovery of nearby devices, which may also need location permissions.")
                        Spacer(modifier = Modifier.size(16.dp))
                        Button(
                            onClick = {
                                permissionsNeeded.launchMultiplePermissionRequest()
                            }
                        ) {
                            Text(text = "Request permissions")
                        }
                    }
                }

                ProcessStateConstants.PERMISSIONS_GRANTED -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "All permissions OK, ready to advertise.")
                    }
                }

                ProcessStateConstants.PERMISSIONS_DENIED -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "Without the necessary permissions, importing from nearby devices is not possible.")
                        Spacer(modifier = Modifier.size(16.dp))
                        Button(
                            onClick = {
                                permissionsNeeded.launchMultiplePermissionRequest()
                            }
                        ) {
                            Text(text = "Request permissions")
                        }
                    }
                }

                ProcessStateConstants.ADVERTISING -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "All permissions OK, waiting for devices...")
                    }
                }

                ProcessStateConstants.DISCOVERED -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "We have been discovered!")
                    }
                }

                ProcessStateConstants.AUTHENTICATED_OK -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "Authentication OK")
                    }
                }
                ProcessStateConstants.AUTHENTICATED_DENIED -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "Authentication denied")
                    }
                }
                ProcessStateConstants.CONNECTION_ESTABLISHED, ProcessStateConstants.PROCESSES_SELECTED -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "Connection has been established")
                    }
                }

                ProcessStateConstants.CONNECTION_FAILED -> TODO()
                ProcessStateConstants.RECEIVING -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "We are receiving data!")
                    }
                }

                ProcessStateConstants.DISCONNECTED -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "Disconnected. All good.")
                    }
                }

                ProcessStateConstants.ERROR -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "Some error occurred. It may help to move away from this screen and try it all again.")
                    }
                }

                ProcessStateConstants.DONE -> TODO()
            }
        }
    }
}