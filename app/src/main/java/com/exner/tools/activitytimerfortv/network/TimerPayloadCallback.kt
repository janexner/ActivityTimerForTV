package com.exner.tools.activitytimerfortv.network

import android.content.Context
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate

class TimerPayloadCallback(
    val context: Context
) : PayloadCallback() {
    override fun onPayloadReceived(p0: String, p1: Payload) {
        TODO("Not yet implemented")
    }

    override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
        TODO("Not yet implemented")
    }
}