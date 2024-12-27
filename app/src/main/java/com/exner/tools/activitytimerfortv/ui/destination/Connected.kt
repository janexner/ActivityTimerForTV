package com.exner.tools.activitytimerfortv.ui.destination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.network.Permissions
import com.exner.tools.activitytimerfortv.ui.ConnectedProcessStateConstants
import com.exner.tools.activitytimerfortv.ui.ConnectedViewModel
import com.exner.tools.activitytimerfortv.ui.tools.BodyText
import com.exner.tools.activitytimerfortv.ui.tools.DefaultSpacer
import com.exner.tools.activitytimerfortv.ui.tools.IconSpacer
import com.exner.tools.activitytimerfortv.ui.tools.StandardButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.nearby.Nearby
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalPermissionsApi::class)
@Destination<RootGraph>
@Composable
fun Connected(
    connectedViewModel: ConnectedViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val permissions = Permissions(context = context)
    val permissionsNeeded =
        rememberMultiplePermissionsState(permissions = permissions.getAllNecessaryPermissionsAsListOfStrings())

    val processState by connectedViewModel.processStateFlow.collectAsState()

    val listState = rememberLazyListState()

    val openAuthenticationDialog = remember { mutableStateOf(false) }
    val connectionInfo by connectedViewModel.connectionInfo.collectAsState()

    connectedViewModel.provideConnectionsClient(Nearby.getConnectionsClient(context))

    // some sanity checking for state
    if (processState.currentState == ConnectedProcessStateConstants.IDLE) {
        connectedViewModel.transitionToNewState(ConnectedProcessStateConstants.AWAITING_PERMISSIONS)
    } else if (processState.currentState == ConnectedProcessStateConstants.AWAITING_PERMISSIONS && permissionsNeeded.allPermissionsGranted) {
        connectedViewModel.transitionToNewState(ConnectedProcessStateConstants.PERMISSIONS_GRANTED)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 24.dp)
    ) {
        // buttons
        Row {
            when (processState.currentState) {
                ConnectedProcessStateConstants.AWAITING_PERMISSIONS,
                ConnectedProcessStateConstants.PERMISSIONS_DENIED -> {
                    StandardButton(
                        onClick = {
                            permissionsNeeded.launchMultiplePermissionRequest()
                        },
                        imageVector = Icons.Default.CheckCircle,
                        text = "Request permissions"
                    )
                }

                ConnectedProcessStateConstants.PERMISSIONS_GRANTED,
                ConnectedProcessStateConstants.CANCELLED,
                ConnectedProcessStateConstants.DONE -> {
                    Button(
                        enabled = permissionsNeeded.allPermissionsGranted,
                        onClick = {
                            connectedViewModel.transitionToNewState(
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
            if (processState.currentState == ConnectedProcessStateConstants.CANCELLED || processState.currentState == ConnectedProcessStateConstants.DONE || processState.currentState == ConnectedProcessStateConstants.AWAITING_PERMISSIONS) {
                StandardButton(
                    onClick = {
                        navigator.navigateUp()
                    },
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    text = "Go back"
                )
            } else {
                StandardButton(
                    onClick = {
                        connectedViewModel.transitionToNewState(
                            ConnectedProcessStateConstants.CANCELLED,
                            "Cancelled by user"
                        )
                    },
                    imageVector = Icons.Default.Clear,
                    text = "Cancel"
                )
            }
        }
        // spacer
        DefaultSpacer()
        Text(
            text = "Status: ${connectedViewModel.messages[connectedViewModel.messages.size - 1]}",
            style = MaterialTheme.typography.bodyLarge
        )
        DefaultSpacer()
        BodyText(text = "Log:")
        LazyColumn(
            modifier = Modifier,
            state = listState,
            reverseLayout = true
        ) {
            items(items = connectedViewModel.messages) { message ->
                Spacer(modifier = Modifier.padding(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(0.8f)
                    )
                    Icon(imageVector = Icons.Default.Check, contentDescription = "OK")
                }
            }
        }

        if (processState.currentState == ConnectedProcessStateConstants.AUTHENTICATION_REQUESTED) {
            openAuthenticationDialog.value = true
            ProcessStateAuthenticationRequestedScreen(
                openAuthenticationDialog = openAuthenticationDialog.value,
                info = connectionInfo,
                confirmCallback = {
                    openAuthenticationDialog.value = false
                    connectedViewModel.transitionToNewState(
                        ConnectedProcessStateConstants.AUTHENTICATION_OK,
                        "Accepted"
                    )
                },
                dismissCallback = {
                    openAuthenticationDialog.value = false
                    connectedViewModel.transitionToNewState(
                        ConnectedProcessStateConstants.AUTHENTICATION_DENIED,
                        "Denied"
                    )
                }
            )
        }
    }
}