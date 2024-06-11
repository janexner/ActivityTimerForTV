package com.exner.tools.activitytimerfortv.ui.destination

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
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
import com.exner.tools.activitytimerfortv.ui.ImportFromNearbyDeviceViewModel
import com.exner.tools.activitytimerfortv.ui.ProcessStateConstants
import com.exner.tools.activitytimerfortv.ui.tools.ActivityTimerNavigationDrawerContent
import com.exner.tools.activitytimerfortv.network.Permissions
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
    val permissionsNeeded = rememberMultiplePermissionsState(permissions = permissions.getAllNecessaryPermissionsAsListOfStrings())

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
                    enabled = processState.currentState == ProcessStateConstants.CONNECTED,
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
                Spacer(modifier = Modifier.weight(0.5f))
                if (processState.currentState == ProcessStateConstants.PERMISSIONS_GRANTED) {
                    WideButton(
                        enabled = permissionsNeeded.allPermissionsGranted,
                        onClick = {
                            try {
                                importFromNearbyDeviceViewModel.setCurrentState(
                                    ProcessStateConstants.AWAITING_DISCOVERY
                                )
                                importFromNearbyDeviceViewModel.startAdvertising(context = context)
                            } catch (se: SecurityException) {
                                Log.e("NEARBY", "Unable to advertise: " + se.cause)
                                importFromNearbyDeviceViewModel.setCurrentState(
                                    ProcessStateConstants.ERROR
                                )
                            }
                        },
                        title = { Text(text = "Discover Devices") },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Call,
                                contentDescription = "Discover Devices"
                            )
                        }
                    )
                } else if (processState.currentState == ProcessStateConstants.AWAITING_DISCOVERY) {
                    WideButton(
                        enabled = permissionsNeeded.allPermissionsGranted,
                        onClick = {
                            try {
                                importFromNearbyDeviceViewModel.setCurrentState(
                                    ProcessStateConstants.DISCONNECTED
                                )
                                importFromNearbyDeviceViewModel.stopAdvertising(context = context)
                            } catch (se: SecurityException) {
                                Log.e("NEARBY", "Unable to advertise: " + se.cause)
                                importFromNearbyDeviceViewModel.setCurrentState(
                                    ProcessStateConstants.ERROR
                                )
                            }
                        },
                        title = { Text(text = "Cancel Discovery") },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Call,
                                contentDescription = "Cancel Discovery"
                            )
                        }
                    )
                }
            }
            // spacer
            Spacer(modifier = Modifier.weight(0.1f))

            // some sanity checking for state
            if (processState.currentState == ProcessStateConstants.AWAITING_PERMISSIONS && permissionsNeeded.allPermissionsGranted) {
                importFromNearbyDeviceViewModel.setCurrentState(ProcessStateConstants.PERMISSIONS_GRANTED)
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
                ProcessStateConstants.AWAITING_DISCOVERY -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "All permissions OK, waiting for devices...")
                    }
                }
                ProcessStateConstants.DISCOVERED -> TODO()
                ProcessStateConstants.CONNECTED -> TODO()
                ProcessStateConstants.RECEIVING -> TODO()
                ProcessStateConstants.DISCONNECTED -> TODO()
                ProcessStateConstants.ERROR -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "Some error occurred. It may help to move away from this screen and try it all again.")
                    }
                }
            }
        }
    }
}