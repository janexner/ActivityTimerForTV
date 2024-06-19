package com.exner.tools.activitytimerfortv.network

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes

class TimerConnectionLifecycleCallback(
    val context: Context,
    val connectionAuthenticationUICallback: () -> Boolean
) : ConnectionLifecycleCallback() {
    override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
        Log.d("TCLC", "On Connection Initiated... $endpointId / $connectionInfo")

        val endpoint = TimerEndpoint(endpointId, connectionInfo.endpointName)
        val connectionsClient = Nearby.getConnectionsClient(context)

        if (connectionAuthenticationUICallback()) {
            // not sure what to do here, tbh
            Log.d("TCLC", "Main body of authenticationcallback if statement")
            true
        }
    }

    override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
        Log.d("TCLC", "On Connection Result... $endpointId / ${result.status}")
        when (result.status.statusCode) {
            ConnectionsStatusCodes.STATUS_OK -> {}
            ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {}
            ConnectionsStatusCodes.STATUS_ERROR -> {}
            else -> {}
        }
    }

    override fun onDisconnected(p0: String) {
        Log.d("TCLC", "On Disconnection... $p0")
    }
}