package com.exner.tools.activitytimerfortv.ui.destination.wrappers

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import com.exner.tools.activitytimerfortv.network.Permissions
import com.exner.tools.activitytimerfortv.ui.tools.StandardButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.wrapper.DestinationWrapper

object AskForPermissionsOnTVWrapper : DestinationWrapper {

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun <T> DestinationScope<T>.Wrap(screenContent: @Composable () -> Unit) {

        val context = LocalContext.current
        val permissions = Permissions(context = context)
        val permissionsNeeded =
            rememberMultiplePermissionsState(
                permissions = permissions.getAllNecessaryPermissionsAsListOfStrings(),
                onPermissionsResult = { results ->
                    results.forEach { result ->
                        Log.d("PGW PERMISSIONS", "${result.key} : ${result.value}")
                    }
                }
            )

        // UI when all is good
        if (permissionsNeeded.allPermissionsGranted) {
            screenContent()
        } else {
            // UI when we need permissions
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp, vertical = 24.dp)
            ) {
                // buttons
                Row {
                    StandardButton(
                        onClick = {
                            permissionsNeeded.launchMultiplePermissionRequest()
                        },
                        imageVector = Icons.Default.CheckCircle,
                        text = "Request permissions"
                    )
                }
                Spacer(modifier = Modifier.weight(0.1f))
                Text(text = "If you would like to receive processes from your phone, this app needs permission for Bluetooth, WiFi, and the discovery of nearby devices, which may also need location permissions.")
            }
        }
    }
}